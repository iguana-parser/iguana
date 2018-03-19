package org.iguana.sppf;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.VisitResult;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;

import static org.iguana.parsetree.VisitResult.ebnf;
import static org.iguana.parsetree.VisitResult.empty;
import static org.iguana.parsetree.VisitResult.single;

public class SPPFParseTreeVisitor implements SPPFVisitor<VisitResult> {

    private final ParseTreeBuilder parseTreeBuilder;
    private final Set<NonterminalNode> visitedNodes;
    private final Map<NonPackedNode, VisitResult> convertedNodes;

    public SPPFParseTreeVisitor(ParseTreeBuilder parseTreeBuilder) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.convertedNodes = new HashMap<>();
        this.visitedNodes = new LinkedHashSet<>();
    }

    @Override
    public VisitResult visit(TerminalNode node) {
        return convertedNodes.computeIfAbsent(node, key -> {
                    Object terminalNode = parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getRightExtent());
                    if (terminalNode == null) return empty();
                    return single(terminalNode);
                }
        );
    }

    @Override
    public VisitResult visit(org.iguana.sppf.NonterminalNode node) {
        VisitResult result = convertedNodes.get(node);
        if (result != null) return result;

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
            throw new CyclicGrammarException(cycle);
        } else {
            visitedNodes.add(node);
        }

        if (node.isAmbiguous()) {
            Set<Object> children = new HashSet<>();
            for (PackedNode packedNode : node.getChildren()) {
                children.addAll(create(packedNode));
            }
            result = single(parseTreeBuilder.ambiguityNode(children));
        } else {
            PackedNode packedNode = node.getChildAt(0);
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                case Layout:
                case Start: {
                    List<Object> children = create(packedNode);
                    if (children.size() > 1) {
                        result = single(parseTreeBuilder.ambiguityNode(new HashSet<>(children)));
                    } else {
                        result = single(children.get(0));
                    }
                    break;
                }

                case Star: {
                    Symbol symbol = packedNode.getGrammarSlot().getPosition().getRule().getDefinition();
                    VisitResult visitResult = packedNode.accept(this);
                    result = single(parseTreeBuilder.metaSymbolNode(symbol, visitResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                    break;
                }

                case Plus: {
                    Symbol symbol = packedNode.getGrammarSlot().getPosition().getRule().getDefinition();
                    VisitResult visitResult = packedNode.accept(this);
                    result = ebnf(visitResult.getValues(), symbol);
                    break;
                }

                case Seq:
                case Alt:
                case Opt: {
                    Symbol symbol = packedNode.getGrammarSlot().getPosition().getRule().getDefinition();
                    VisitResult visitResult = packedNode.accept(this);
                    result = single(parseTreeBuilder.metaSymbolNode(symbol, visitResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                    break;
                }
            }
        }
        visitedNodes.remove(node);
        convertedNodes.put(node, result);
        return result;
    }

    @Override
    public VisitResult visit(IntermediateNode node) {
        VisitResult result = convertedNodes.get(node);
        if (result != null) return result;

        if (node.isAmbiguous()) {
            result = empty();
            for (PackedNode packedNode : node.getChildren()) {
                result = result.merge(packedNode.accept(this));
            }
        } else {
            PackedNode packedNode = node.getChildAt(0);
            result = packedNode.accept(this);
        }
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

        return left.merge(right);
    }

    List<Object> create(PackedNode packedNode) {
        List<Object> result = new ArrayList<>();
        VisitResult visitResult = packedNode.accept(this);
        if (visitResult instanceof VisitResult.ListOfResult) {
            for (VisitResult vResult : ((VisitResult.ListOfResult) visitResult).getVisitResults()) {
                result.add(parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(), vResult.getValues(), packedNode.getLeftExtent(), packedNode.getRightExtent()));
            }
        } else {
            Rule rule = packedNode.getGrammarSlot().getPosition().getRule();
            switch (rule.getHead().getNodeType()) {
                case Start:
                    result.add(parseTreeBuilder.metaSymbolNode(rule.getHead(), visitResult.getValues(), packedNode.getLeftExtent(), packedNode.getRightExtent()));
                    break;

                case Basic:
                    result.add(parseTreeBuilder.nonterminalNode(rule, visitResult.getValues(), packedNode.getLeftExtent(), packedNode.getRightExtent()));
                    break;

                default:
                    throw new RuntimeException("Unexpected nonterminal type");
            }
        }
        return result;
    }

}
