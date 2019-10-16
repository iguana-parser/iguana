package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sequence implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Symbol> symbols;

    public Associativity associativity;

    public String label;

    public Sequence() { }

    public Sequence(Symbol symbol, List<Symbol> rest, Associativity associativity, String label) {
        this.symbols = new ArrayList<>();
        if (symbol != null) symbols.add(symbol);
        if (rest != null) symbols.addAll(rest);
        this.associativity = associativity;
        this.label = label;
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
}
