package org.iguana.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sequence {

    private final List<Symbol> symbols;

    public final Associativity associativity;

    public final String label;

    public Sequence(Builder builder) {
        this.symbols = builder.symbols;
        this.associativity = builder.associativity;
        this.label = builder.label;
    }

    public boolean isEmpty() {
        return symbols == null || symbols.isEmpty();
    }

    public Symbol first() {
        if (symbols == null || symbols.isEmpty()) return null;
        return symbols.get(0);
    }

    public List<Symbol> rest() {
        if (symbols == null || symbols.isEmpty() || symbols.size() == 1) return null;
        return symbols.subList(1, symbols.size());
    }

    public List<Symbol> getSymbols() {
        if (symbols == null) return Collections.emptyList();
        return symbols;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sequence)) return false;
        Sequence other = (Sequence) obj;
        return Objects.equals(this.symbols, other.symbols) &&
               Objects.equals(this.associativity, other.associativity) &&
               Objects.equals(this.label, other.label);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Symbol> symbols = getSymbols();
        for (Symbol symbol : symbols) {
            sb.append(symbol.toString()).append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static class Builder {
        private List<Symbol> symbols = new ArrayList<>();
        private Associativity associativity;
        private String label;

        public Builder addSymbol(Symbol symbol) {
            this.symbols.add(symbol);
            return this;
        }

        public Builder addSymbols(List<Symbol> symbols) {
            this.symbols.addAll(symbols);
            return this;
        }

        public Builder setAssociativity(Associativity associativity) {
            this.associativity = associativity;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Sequence build() {
            return new Sequence(this);
        }
    }
}
