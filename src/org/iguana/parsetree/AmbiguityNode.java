package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;

import java.util.Set;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static iguana.utils.Assert.*;

public class AmbiguityNode implements ParseTreeNode {

    private final Set<ParseTreeNode> alternatives;

    public AmbiguityNode(Set<ParseTreeNode> alternatives) {
        this.alternatives = requireNonNull(alternatives);
        requireNonEmpty(alternatives);
    }

    @Override
    public int start() {
        return alternatives.iterator().next().start();
    }

    @Override
    public int end() {
        return alternatives.iterator().next().end();
    }

    @Override
    public String text(Input input) {
        return input.subString(start(), end());
    }

    @Override
    public Iterable<ParseTreeNode> children() {
        return alternatives;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Rule definition() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AmbiguityNode)) return false;
        AmbiguityNode other = (AmbiguityNode) obj;
        return alternatives.equals(other.alternatives);
    }

    @Override
    public int hashCode() {
        return hash(alternatives);
    }
}
