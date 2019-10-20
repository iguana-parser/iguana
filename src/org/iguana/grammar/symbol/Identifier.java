package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

public class Identifier extends AbstractSymbol {

    public static Identifier fromName(String name) {
        return new Builder().setName(name).build();
    }

    public Identifier(Builder builder) {
        super(builder);
    }

    @Override
    public Builder copy() {
        return new Builder();
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static class Builder extends SymbolBuilder<Identifier> {
        @Override
        public Identifier build() {
            return new Identifier(this);
        }
    }
}
