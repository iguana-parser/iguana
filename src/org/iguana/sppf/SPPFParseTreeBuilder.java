package org.iguana.sppf;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parsetree.AmbiguityNode;
import org.iguana.parsetree.ListNode;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.traversal.SPPFVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SPPFParseTreeBuilder implements SPPFVisitor<List<ParseTreeNode<?>>> {

    private final Input input;

    public SPPFParseTreeBuilder(Input input) {
        this.input = input;
    }

    @Override
    public List<ParseTreeNode<?>> visit(TerminalNode node) {
        List<ParseTreeNode<?>> list = new ArrayList<>();
        list.add(new org.iguana.parsetree.TerminalNode(input, node.getLeftExtent(), node.getRightExtent(), node.getGrammarSlot().getTerminal()));
        return list;
    }

    @Override
    public List<ParseTreeNode<?>> visit(org.iguana.sppf.NonterminalNode node) {
        List<ParseTreeNode<?>> result = new ArrayList<>();
        if (node.isAmbiguous()) {
            Set<NonterminalNode> children = new HashSet<>();
            for (PackedNode packedNode : node.getChildren()) {
                Rule rule = ((EndGrammarSlot)packedNode.getChildAt(0).getGrammarSlot()).getPosition().getRule();
                children.add(new NonterminalNode(
                        input,
                        rule,
                        packedNode.accept(this),
                        node.getLeftExtent(),
                        node.getRightExtent()
                ));
            }
            result.add(new AmbiguityNode(input, children));
        } else {
            switch (node.getGrammarSlot().getNodeType()) {
                case Basic:
                    List<ParseTreeNode<?>> children = new ArrayList<>();
                    for (PackedNode packedNode : node.getChildren()) {
                        children.addAll(packedNode.accept(this));
                    }

                    result.add(new NonterminalNode(
                            input,
                            node.getChildAt(0).getGrammarSlot().getPosition().getRule(),
                            children,
                            node.getLeftExtent(),
                            node.getRightExtent()));
            }
        }
        return result;
    }

    @Override
    public List<ParseTreeNode<?>> visit(IntermediateNode node) {
        if (node.isAmbiguous()) {
            List<ParseTreeNode<?>> result = new ArrayList<>();
            Set<ListNode> children = new HashSet<>();
            for (PackedNode packedNode : node.getChildren()) {
                children.add(new ListNode(input, packedNode.accept(this)));
            }
            result.add(new AmbiguityNode(input, children));
            return result;
        } else {
            return node.getChildAt(0).accept(this);
        }
    }

    @Override
    public List<ParseTreeNode<?>> visit(PackedNode node) {
        List<ParseTreeNode<?>> left = node.getLeftChild().accept(this);
        if (node.getRightChild() != null) {
            List<ParseTreeNode<?>> right = node.getRightChild().accept(this);
            left.addAll(right);
        }
        return left;
    }

}


