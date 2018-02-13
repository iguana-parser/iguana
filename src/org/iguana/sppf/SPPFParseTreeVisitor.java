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

    private final Input input;
    private final ParseTreeBuilder parseTreeBuilder;

    public SPPFParseTreeVisitor(Input input, ParseTreeBuilder parseTreeBuilder) {
        this.input = input;
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
//            parseTreeBuilder.nonterminalNode()
//            Set<NonterminalNode> children = new HashSet<>();
//            for (PackedNode packedNode : node.getChildren()) {
//                Rule rule = ((EndGrammarSlot)packedNode.getChildAt(0).getGrammarSlot()).getPosition().getRule();
//                children.add(new NonterminalNode(
//                        input,
//                        rule,
//                        packedNode.accept(this),
//                        node.getLeftExtent(),
//                        node.getRightExtent()
//                ));
//            }
//            result.add(new AmbiguityNode(input, children));
        } else {
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                    PackedNode packedNode = node.getChildAt(0);
                    List<Object> children = new ArrayList<>(packedNode.accept(this));

                    Object nonterminalNode = parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getPosition().getRule(),
                            children, node.getLeftExtent(), node.getRightExtent());
                    result.add(nonterminalNode);
            }
        }
        return result;
    }

    @Override
    public List<Object> visit(IntermediateNode node) {
        Stream<Object> left = node.getChildAt(0).accept(this).stream();
        Stream<Object> children;
        if (node.childrenCount() == 2) {
            Stream<Object> right = node.getChildAt(2).accept(this).stream();
            children = Stream.concat(left, right);
        } else {
            children = left;
        }
        return children.filter(Objects::nonNull).collect(Collectors.toList());
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

    private static List<Object> merge(List<Object> list1, List<Object> list2) {
        if (list1 == null || list2 == null) throw new IllegalArgumentException("list1 or list2 cannot be empty");
        return Stream.concat(list1.stream(), list2.stream()).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
