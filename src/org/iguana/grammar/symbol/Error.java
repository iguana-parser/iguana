package org.iguana.grammar.symbol;

import io.usethesource.capsule.Set;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Error implements Symbol {
    @Override
    public Set.Immutable<String> getEnv() {
        return Set.Immutable.of();
    }

    @Override
    public void setEnv(Set.Immutable<String> env) {
    }

    @Override
    public void setEmpty() {
    }

    @Override
    public String getName() {
        return "empty";
    }

    @Override
    public List<Condition> getPreConditions() {
        return Collections.emptyList();
    }

    @Override
    public List<Condition> getPostConditions() {
        return Collections.emptyList();
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public SymbolBuilder<? extends Symbol> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "error";
    }
}
