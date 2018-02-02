package org.iguana.sppf;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parsetree.*;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.traversal.SPPFVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        list.add(parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getRightExtent(), input));
        return list;
    }

    @Override
    public List<Object> visit(org.iguana.sppf.NonterminalNode node) {
        List<Object> result = new ArrayList<>();
//        if (node.isAmbiguous()) {
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
//        } else {
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                    List<Object> children = new ArrayList<>();
                    for (PackedNode packedNode : node.getChildren()) {
                        children.addAll(packedNode.accept(this));
                    }

                    Object nonterminalNode = parseTreeBuilder.nonterminalNode(node.getChildAt(0).getGrammarSlot().getPosition().getRule(),
                            children, node.getLeftExtent(), node.getRightExtent(), input);
                    result.add(nonterminalNode);
            }
//        }
        return result;
    }

    @Override
    public List<Object> visit(IntermediateNode node) {
//        if (node.isAmbiguous()) {
//            List<Object> result = new ArrayList<>();
//            Set<ListNode> children = new HashSet<>();
//            for (PackedNode packedNode : node.getChildren()) {
//                children.add(new ListNode(input, packedNode.accept(this)));
//            }
//            result.add(new AmbiguityNode(input, children));
//            return result;
//        } else {
        return node.getChildAt(0).accept(this);
//        }
    }

    @Override
    public List<Object> visit(PackedNode node) {
        List<Object> left = node.getLeftChild().accept(this);
        if (node.getRightChild() != null) {
            List<Object> right = node.getRightChild().accept(this);
            left.addAll(right);
        }
        return left;
    }

}


