package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

public class Identifier extends AbstractSymbol {

    private static final long serialVersionUID = 1L;

    public static Identifier fromName(String name) {
        return new Builder(name).build();
    }

    public Identifier(Builder builder) {
        super(builder);
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

        private Builder() { }

        public Builder(String name) {
            this.name = name;
        }

        public Builder(Identifier identifier) {
            super(identifier);
        }

        @Override
        public Identifier build() {
            return new Identifier(this);
        }
    }
}
