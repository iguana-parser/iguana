package org.iguana.sppf;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parsetree.*;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;
import java.util.stream.Collectors;

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
            Set<Object> children = node.getChildren().stream().map(
                    packedNode -> parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(), packedNode.accept(this).getValues(), node.getLeftExtent(), node.getRightExtent()))
                    .collect(Collectors.toSet());
            return single(parseTreeBuilder.ambiguityNode(children));
        } else {
            PackedNode packedNode = node.getChildAt(0);
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                case Layout:
                    VisitResult visitResult = packedNode.accept(this);
                    if (visitResult instanceof VisitResult.ListOfResult) {
                        Set<Object> ambiguities = new HashSet<>();
                        for (VisitResult vResult : ((VisitResult.ListOfResult) visitResult).getVisitResults()) {
                            ambiguities.add(parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(), vResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                        }
                        result = single(parseTreeBuilder.ambiguityNode(ambiguities));

                    } else {
                        result = single(parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(), visitResult.getValues(), node.getLeftExtent(), node.getRightExtent()));
                    }
                    break;

                case Star:
                case Plus:
//                    Object ebnfNode = parseTreeBuilder.metaSymbolNode(
//                            packedNode.getGrammarSlot().getPosition().getRule().getDefinition(),
//                            packedNode.accept(this),
//                            node.getLeftExtent(),
//                            node.getRightExtent());
//                    result.add(ebnfNode);
                    break;
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
            for (PackedNode packedNode: node.getChildren()) {
                result = result.merge(packedNode.accept(this));
            }
        } else {
            PackedNode packedNode = node.getChildAt(0);
            result = packedNode.accept(this);
        }
        convertedNodes.put(node, result);
        result.setObject(node.getGrammarSlot());
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

}
