package org.iguana.grammar.symbol;

import java.util.*;

public class Sequence {

    private final List<Symbol> symbols;

    public final Associativity associativity;

    public final String label;

    private final Map<String, Object> attributes;

    public Sequence(Builder builder) {
        this.symbols = builder.symbols;
        this.associativity = builder.associativity;
        this.label = builder.label;
        this.attributes = builder.attributes;
    }

    public boolean isEmpty() {
        return symbols.isEmpty();
    }

    public Symbol first() {
        if (symbols.isEmpty()) return null;
        return symbols.get(0);
    }

    public List<Symbol> rest() {
        if (symbols.isEmpty() || symbols.size() == 1) return null;
        return symbols.subList(1, symbols.size());
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
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
        if (associativity != null && associativity != Associativity.UNDEFINED) {
            sb.append(associativity).append(" ");
        }
        for (Symbol symbol : symbols) {
            sb.append(symbol).append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        if (label != null) {
            sb.append("  ").append("%").append(label);
        }
        if (!attributes.isEmpty()) {
            sb.append("  {");
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (entry.getValue() == null) {
                    sb.append(String.format("%s ", entry.getKey()));
                } else {
                    sb.append(String.format("%s=%s ", entry.getKey(), entry.getValue()));
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
        }
        return sb.toString();
    }

    public static class Builder {
        private final Map<String, Object> attributes = new HashMap<>();
        private final List<Symbol> symbols = new ArrayList<>();
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

        public Builder addAttribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public Sequence build() {
            return new Sequence(this);
        }
    }
}
