package org.iguana.parsetree;

import org.iguana.grammar.symbol.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static iguana.utils.Assert.requireNonEmpty;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class AmbiguityNode implements ParseTreeNode {

    private final List<ParseTreeNode> alternatives;

    public AmbiguityNode(Set<ParseTreeNode> alternatives) {
        this.alternatives = new ArrayList<>(requireNonNull(alternatives));
        requireNonEmpty(alternatives);
    }

    @Override
    public int getStart() {
        return alternatives.iterator().next().getStart();
    }

    @Override
    public int getEnd() {
        return alternatives.iterator().next().getEnd();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<ParseTreeNode> children() {
        return alternatives;
    }

    @Override
    public <R> R accept(ParseTreeVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Rule getGrammarDefinition() {
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

    @Override
    public String toString() {
        return "{" + alternatives.toString() + "}";
    }
}
