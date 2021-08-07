package org.iguana.traversal;

import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.MetaSymbolNode;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.VisitResult;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.*;

import java.util.List;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.iguana.parsetree.VisitResult.*;

public class AmbiguousSPPFToParseTreeVisitor<T> implements SPPFVisitor<VisitResult> {
    private final static Object DUMMY = "null";

    private final ParseTreeBuilder<T> parseTreeBuilder;
    private final Set<NonterminalNode> visitedNodes;
    private final Map<NonPackedNode, VisitResult> convertedNodes;

    private final boolean ignoreLayout;
    private final ParserResultOps resultOps;

    private final VisitResult.CreateParseTreeVisitor<T> createNodeVisitor;

    public AmbiguousSPPFToParseTreeVisitor(ParseTreeBuilder<T> parseTreeBuilder, boolean ignoreLayout, ParserResultOps resultOps) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.ignoreLayout = ignoreLayout;
        this.resultOps = resultOps;
        this.convertedNodes = new HashMap<>();
        this.visitedNodes = new LinkedHashSet<>();
        this.createNodeVisitor = new VisitResult.CreateParseTreeVisitor<>(parseTreeBuilder);
    }

    @Override
    public VisitResult visit(TerminalNode node) {
        if (ignoreLayout && node.getGrammarSlot().getTerminal().getNodeType() == TerminalNodeType.Layout) {
            return empty();
        }
        return convertedNodes.computeIfAbsent(node, key -> {
//                    if (node.getLeftExtent() == node.getIndex()) return empty();
                    Object terminalNode = parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getIndex());
                    return unpack(single(terminalNode));
                }
        );
    }

    @Override
    public VisitResult visit(org.iguana.sppf.NonterminalNode node) {
        if (ignoreLayout && node.getGrammarSlot().getNonterminal().getNodeType() == NonterminalNodeType.Layout) {
            return empty();
        }

        VisitResult result = convertedNodes.get(node);
        if (result != null) {
//            if (!visitedNodes.contains(node)) {
//                result = unpack(result);
//            }
            return result;
        }

        result = single(DUMMY);
        convertedNodes.put(node, result);

        // To guard for cyclic SPPFs
        if (visitedNodes.contains(node)) {
            List<Nonterminal> cycle = new ArrayList<>();
            boolean seen = false;
            for (NonterminalNode n : visitedNodes) {
                if (seen) {
                    cycle.add(n.getGrammarSlot().getNonterminal());
                } else {
                    if (n == node) {
                        cycle.add(n.getGrammarSlot().getNonterminal());
                        seen = true;
                    }
                }
            }
            cycle.add(node.getGrammarSlot().getNonterminal());
//            return node;
//            throw new CyclicGrammarException(cycle);
        } else {
            visitedNodes.add(node);
        }

        if (node.isAmbiguous()) {
            Set<T> children = new HashSet<>();
            for (PackedNode packedNode : resultOps.getPackedNodes(node)) {
                VisitResult visitResult = packedNode.accept(this);
                children.addAll(visitResult.accept(createNodeVisitor, packedNode));
            }
            ((Single) result).setValue(parseTreeBuilder.ambiguityNode(children));
        } else {
            PackedNode packedNode = node.getFirstPackedNode();
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                case Layout: {
                    List<T> children = packedNode.accept(this).accept(createNodeVisitor, packedNode);
                    if (children.size() > 1) {
//                        result = single(parseTreeBuilder.ambiguityNode(new HashSet<>(children)));
                        ((Single) result).setValue(parseTreeBuilder.ambiguityNode(new HashSet<>(children)));
                    } else {
                        T child = children.get(0);
                        if (child instanceof MetaSymbolNode) { // Last Plus node propagated up
//                            result = single(parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getRule(), children, packedNode.getLeftExtent(), packedNode.getRightExtent()));
                            ((Single) result).setValue(parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getRule(),
                                    children, packedNode.getLeftExtent(), packedNode.getRightExtent()));

                        } else {
//                            result = single(children.get(0));
                            ((Single) result).setValue(children.get(0));
                        }
                    }
                    break;
                }

                case Plus: {
                    Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                    VisitResult visitResult = packedNode.accept(this);
                    result = ebnf(visitResult.getValues(), symbol);
                    break;
                }

                case Star:
                case Seq:
                case Alt:
                case Opt:
                case Start: {
                    Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                    VisitResult visitResult = packedNode.accept(this);
                    // This case handles X+ nodes under other EBNF nodes (See Test 14)
                    if (visitResult instanceof VisitResult.List && visitResult.getValues().size() == 1 && visitResult.getValues().get(0) instanceof VisitResult.EBNF) {
                        VisitResult.EBNF ebnfChild = (VisitResult.EBNF) visitResult.getValues().get(0);
                        T ebnfResult = parseTreeBuilder.metaSymbolNode(ebnfChild.getSymbol(), (List<T>) ebnfChild.getValues(), node.getLeftExtent(), node.getRightExtent());
//                        result = single(parseTreeBuilder.metaSymbolNode(symbol, singletonList(ebnfResult), node.getLeftExtent(), node.getRightExtent()));
                        ((Single) result).setValue(parseTreeBuilder.metaSymbolNode(symbol, singletonList(ebnfResult), node.getLeftExtent(), node.getRightExtent()));

                    } else {
//                        result = single(parseTreeBuilder.metaSymbolNode(symbol, (List<T>) visitResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                        ((Single) result).setValue(parseTreeBuilder.metaSymbolNode(symbol, (List<T>) visitResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                    }

                    break;
                }
            }
        }
        visitedNodes.remove(node);
        result = unpack(result);
        convertedNodes.put(node, result);
        return result;
    }

    private VisitResult unpack(VisitResult result) {
        if (result instanceof Single) {
            return ((Single) result).unpack();
//        } else if (result instanceof VisitResult.List) {
//            List <Object> objects = ((VisitResult.List) result).getValues() ;
//            objects = ((VisitResult.List) result).getValues().stream()
//                    .map(x -> {
//                        if (x instanceof Wrapper) {
//                            return ((Wrapper) x).getValue();
//                        } else {
//                            return x;
//                        }
//                    })
//                    .collect(Collectors.toList());
//            return new VisitResult.List(objects);
//        } else {
        }
        return result;

    }

    @Override
    public VisitResult visit(IntermediateNode node) {
        VisitResult result = convertedNodes.get(node);
        if (result != null) return unpack(result);

        if (node.isAmbiguous()) {
            result = empty();
            for (PackedNode packedNode : resultOps.getPackedNodes(node)) {
                result = result.merge(packedNode.accept(this));
            }
        } else {
            PackedNode packedNode = node.getFirstPackedNode();
            result = packedNode.accept(this);
        }
        result = unpack(result);
        convertedNodes.put(node, result);
        return result;
    }

    @Override
    public VisitResult visit(PackedNode node) {
        VisitResult left = node.getLeftChild().accept(this);
        VisitResult right;
        if (node.getRightChild() != null)
            right = node.getRightChild().accept(this);
        else
            right = empty();

        // It seems that we can simplify the SPPF to ParseTree creation by checking the packed node's node type
        // and may be able to get rid of VisitResult hierarchy
        if (node.getGrammarSlot().getRule().getHead().getNodeType() != NonterminalNodeType.Plus &&
                node.getGrammarSlot().getRule().getHead().getNodeType() != NonterminalNodeType.Star) {
            if (left instanceof EBNF) {
                List<Object> values = new ArrayList<>();
                values.add(left);
                if (right instanceof EBNF) {
                    values.add(right);
                } else {
                    values.addAll(right.getValues());
                }
                return list(values);
            }
        }

        return unpack(left.merge(right));
    }

    public VisitResult getResult(NonterminalNode node) {
        return convertedNodes.get(node);
    }

}
