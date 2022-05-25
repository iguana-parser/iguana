package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

import java.util.HashSet;
import java.util.Set;

public class Identifier extends AbstractSymbol {

    private final Set<String> excepts;

    public static Identifier fromName(String name) {
        return new Builder(name).build();
    }

    public Identifier(Builder builder) {
        super(builder);
        this.excepts = builder.excepts;
    }

    public Set<String> getExcepts() {
        return excepts;
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Identifier)) return false;
        Identifier identifier = (Identifier) obj;
        return identifier.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static class Builder extends SymbolBuilder<Identifier> {

        private Set<String> excepts = new HashSet<>();

        private Builder() { }

        public Builder(String name) {
            this.name = name;
        }

        public Builder(Identifier identifier) {
            super(identifier);
            this.excepts = identifier.excepts;
        }

        public Builder addExcept(String label) {
            excepts.add(label);
            return this;
        }

        @Override
        public Identifier build() {
            return new Identifier(this);
        }
    }
}
