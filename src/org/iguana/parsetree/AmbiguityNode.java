package org.iguana.parsetree;

import org.iguana.grammar.symbol.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class AmbiguityNode implements ParseTreeNode {

    private final Set<ParseTreeNode> alternatives;

    public AmbiguityNode(Set<ParseTreeNode> alternatives) {
        this.alternatives = requireNonNull(alternatives);
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
        return new ArrayList<>(alternatives);
    }

    @Override
    public Object accept(ParseTreeVisitor visitor) {
        return visitor.visitAmbiguityNode(this);
    }

    @Override
    public Rule getGrammarDefinition() {
        return null;
    }

    @Override
    public String getText() {
        if (alternatives.size() == 0) return "";
        return children().get(0).getText();
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
