package org.iguana.sppf;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Star;
import org.iguana.parsetree.*;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class SPPFParseTreeVisitor implements SPPFVisitor<List<Object>> {

    private final ParseTreeBuilder parseTreeBuilder;
    private final Set<NonterminalNode> visitedNodes;
    private final Map<NonPackedNode, List<Object>> convertedNodes;

    public SPPFParseTreeVisitor(ParseTreeBuilder parseTreeBuilder) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.convertedNodes = new HashMap<>();
        this.visitedNodes = new LinkedHashSet<>();
    }

    @Override
    public List<Object> visit(TerminalNode node) {
        return convertedNodes.computeIfAbsent(node, key -> {
            List<Object> list = new ArrayList<>();
            list.add(parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getRightExtent()));
            return list;
        });
    }

    @Override
    public List<Object> visit(org.iguana.sppf.NonterminalNode node) {
        List<Object> result = convertedNodes.get(node);
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

        result = new ArrayList<>();
        if (node.isAmbiguous()) {
            Set<Object> ambiguities = new HashSet<>();
            for (PackedNode packedNode : node.getChildren()) {
                List<Object> children = packedNode.accept(this);

                if (isListOfList(children)) {
                    ambiguities.addAll(
                        children.stream().map(child -> parseTreeBuilder.nonterminalNode(
                            packedNode.getGrammarSlot().getPosition().getRule(),
                            (List<?>) child,
                            node.getLeftExtent(),
                            node.getRightExtent()
                    )).collect(Collectors.toSet()));
                } else {
                    Object nonterminalNode = parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(),
                            children, node.getLeftExtent(), node.getRightExtent());
                    ambiguities.add(nonterminalNode);
                }
            }

            result.add(parseTreeBuilder.ambiguityNode(ambiguities));
        } else {
            PackedNode packedNode = node.getChildAt(0);
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                case Layout:
                    List<Object> children = packedNode.accept(this);

                    // An ambiguity under intermediate node has propagated upwards
                    if (isListOfList(children)) {
                        Set<Object> ambiguties = new HashSet<>();
                        for (Object object : children) {
                            Object nonterminalNode = parseTreeBuilder.nonterminalNode(
                                    packedNode.getGrammarSlot().getPosition().getRule(),
                                    (List<?>) object,
                                    node.getLeftExtent(),
                                    node.getRightExtent()
                            );
                            ambiguties.add(nonterminalNode);
                        }
                        result.add(parseTreeBuilder.ambiguityNode(ambiguties));
                    } else {
                        Object nonterminalNode = parseTreeBuilder.nonterminalNode(
                                packedNode.getGrammarSlot().getPosition().getRule(),
                                children,
                                node.getLeftExtent(),
                                node.getRightExtent());
                        result.add(nonterminalNode);
                    }
                    break;

                case Star:
                    Object starNode = parseTreeBuilder.starNode(
                            (Star) packedNode.getGrammarSlot().getPosition().getRule().getDefinition(),
                            packedNode.accept(this),
                            node.getLeftExtent(),
                            node.getRightExtent());
                    result.add(starNode);
                    break;
            }
        }
        visitedNodes.remove(node);
        convertedNodes.put(node, result);
        return result;
    }

    @Override
    public List<Object> visit(IntermediateNode node) {
        List<Object> ambiguities = convertedNodes.get(node);
        if (ambiguities != null) {
            return ambiguities;
        }
        if (node.isAmbiguous()) {
            ambiguities = new ArrayList<>();
            for (PackedNode packedNode : node.getChildren()) {
                List<Object> visitResult = packedNode.accept(this);
                // This happens when visiting nested ambiguities under intermediate nodes
                // The results should be flattened and a single list should be returned.
                if (isListOfList(visitResult)) {
                    ambiguities.addAll(asListOfList(visitResult));
                } else {
                    ambiguities.add(visitResult);
                }
            }
            return ambiguities;
        } else {
            PackedNode packedNode = node.getChildAt(0);
            ambiguities = packedNode.accept(this);
        }
        convertedNodes.put(node, ambiguities);
        return ambiguities;
    }

    @Override
    public List<Object> visit(PackedNode node) {
        List<Object> left = node.getLeftChild().accept(this);
        List<Object> right;
        if (node.getRightChild() != null)
            right = node.getRightChild().accept(this);
        else
            right = Collections.emptyList();
        return merge(left, right);
    }

    /*
     * Case 1: left: List<Object>, right: List<Object>
     * case 2: List<List<Object>>, right: List<Object>
     *
     */
    private static List<Object> merge(List<Object> list1, List<Object> list2) {
        if (list1 == null || list2 == null) throw new IllegalArgumentException("list1 or list2 cannot be empty");

        List<Object> result = new ArrayList<>();

        if (isListOfList(list1) && !isListOfList(list2)) {
            for (Object object : list1) {
                List<Object> newList = new ArrayList<>();
                addAllNonNull(newList, (List<Object>) object);
                addAllNonNull(newList, list2);
                result.add(newList);
            }
        }

        else if (!isListOfList(list1) && isListOfList(list2)) {
            for (Object object : list2) {
                List<Object> newList = new ArrayList<>();
                addAllNonNull(newList, (List<Object>) object);
                addAllNonNull(newList, list1);
                result.add(newList);
            }
        }

        else if (isListOfList(list1) && isListOfList(list2)) {
            for (Object object1: list1)
                for (Object object2: list2) {
                    List<Object> newList = new ArrayList<>();
                    addAllNonNull(newList, (List<Object>) object1);
                    addAllNonNull(newList, (List<Object>) object2);
                    result.add(newList);
                }
        }

        else {
            addAllNonNull(result, list1);
            addAllNonNull(result, list2);
        }

        return result;
    }

    private static boolean isListOfList(List<Object> list) {
        return list.size() > 0 && list.get(0) instanceof List<?>;
    }

    public static List<List<Object>> asListOfList(Object o) {
        return (List<List<Object>>) o;
    }

    private static <T> void addAllNonNull(List<T> to, List<T> from) {
        for (T o : from) {
            if (o != null)
                to.add(o);
        }
    }

}
