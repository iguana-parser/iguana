package org.iguana.parsetree;

import iguana.utils.input.Input;

import java.util.List;

public class ListNode implements ParseTreeNode<Void> {

    private final Input input;
    private final List<ParseTreeNode<?>> children;

    public ListNode(Input input, List<ParseTreeNode<?>> children) {
        this.input = input;
        this.children = children;
    }

    @Override
    public int start() {
        return children.get(0).start();
    }

    @Override
    public int end() {
        return children.get(children.size() - 1).end();
    }

    @Override
    public String text() {
        return input.subString(start(), end());
    }

    @Override
    public Iterable<? extends ParseTreeNode<?>> children() {
        return children;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Void definition() {
        return null;
    }
}
