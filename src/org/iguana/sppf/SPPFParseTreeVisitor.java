package org.iguana.sppf;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parsetree.*;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.traversal.SPPFVisitor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SPPFParseTreeVisitor implements SPPFVisitor<List<Object>> {

    private final ParseTreeBuilder parseTreeBuilder;

    public SPPFParseTreeVisitor(ParseTreeBuilder parseTreeBuilder) {
        this.parseTreeBuilder = parseTreeBuilder;
    }

    @Override
    public List<Object> visit(TerminalNode node) {
        List<Object> list = new ArrayList<>();
        list.add(parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getRightExtent()));
        return list;
    }

    @Override
    public List<Object> visit(org.iguana.sppf.NonterminalNode node) {
        List<Object> result = new ArrayList<>();
        if (node.isAmbiguous()) {
            Set<Object> ambiguities = new HashSet<>();
            for (PackedNode packedNode : node.getChildren()) {
                List<Object> children = new ArrayList<>(packedNode.accept(this));
                Object nonterminalNode = parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(),
                        children, node.getLeftExtent(), node.getRightExtent());
                ambiguities.add(nonterminalNode);
            }

            Object ambiguityNode = parseTreeBuilder.ambiguityNode(ambiguities);
            result.add(ambiguityNode);
        } else {
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                    PackedNode packedNode = node.getChildAt(0);
                    List<Object> children = new ArrayList<>(packedNode.accept(this));

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
                        Object nonterminalNode = parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(),
                                children, node.getLeftExtent(), node.getRightExtent());
                        result.add(nonterminalNode);
                    }
            }
        }
        return result;
    }

    @Override
    public List<Object> visit(IntermediateNode node) {
        if (node.isAmbiguous()) {
            List<Object> ambiguities = new ArrayList<>();
            for (PackedNode packedNode : node.getChildren()) {
                ambiguities.add(new ArrayList<>(packedNode.accept(this)));
            }
            return ambiguities;
        } else {
            PackedNode packedNode = node.getChildAt(0);
            return new ArrayList<>(packedNode.accept(this));
        }
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

    private static <T> void addAllNonNull(List<T> to, List<T> from) {
        for (T o : from) {
            if (o != null)
                to.add(o);
        }
    }

}
