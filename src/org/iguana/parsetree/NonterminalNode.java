package org.iguana.parsetree;


import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;

import java.util.Collections;
import java.util.List;

public class NonterminalNode implements ParseTreeNode {

    private final Rule rule;
    private final int start;
    private final int end;
    private final List<ParseTreeNode> children;

    public NonterminalNode(Rule rule, List<ParseTreeNode> children, int start, int end) {
        this.rule = rule;
        this.children = children == null ? Collections.emptyList() : children;
        this.start = start;
        this.end = end;
    }

    ParseTreeNode childAt(int i) {
        return children.get(i);
    }

    ParseTreeNode childWithName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public String text(Input input) {
        return input.subString(start, end);
    }

    @Override
    public Iterable<ParseTreeNode> children() {
        return children;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Rule definition() {
        return rule;
    }
}
