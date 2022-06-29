package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import java.util.Collections;
import java.util.List;

import static org.iguana.utils.Assert.requireNonNegative;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class NonterminalNode implements ParseTreeNode {

    private final RuntimeRule rule;
    private final int start;
    private final int end;
    private final List<ParseTreeNode> children;

    public NonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        this.rule = requireNonNull(rule);
        this.children = children == null ? Collections.emptyList() : children;
        this.start = requireNonNegative(start);
        this.end = requireNonNegative(end);
    }

    @Override
    public String getName() {
        return rule.getHead().getName();
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public List<ParseTreeNode> children() {
        return children;
    }

    @Override
    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visitNonterminalNode(this);
    }

    @Override
    public RuntimeRule getGrammarDefinition() {
        return rule;
    }

    @Override
    public String getText() {
        if (children.size() == 1) return children.get(0).getText();

        StringBuilder sb = new StringBuilder();
        for (ParseTreeNode child : children) {
            sb.append(child.getText());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NonterminalNode)) return false;
        NonterminalNode other = (NonterminalNode) obj;
        return this.start == other.start &&
               this.end == other.end &&
               this.rule.equals(other.rule) &&
               this.children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return hash(start, end, rule, children);
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %d)", rule, start, end);
    }
}
