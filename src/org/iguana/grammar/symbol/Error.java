package org.iguana.grammar.symbol;

import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Error extends AbstractAttrs implements Symbol {

    private static final Error instance = new Error();

    public static Error getInstance() {
        return instance;
    }

    private Error() { }

    @Override
    public String getName() {
        return "Error";
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
        return "Error";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == instance;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(instance);
    }
}
