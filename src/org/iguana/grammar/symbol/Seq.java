package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Seq implements Serializable {

    private static final long serialVersionUID = 1L;

    public Symbol first;
    public List<Symbol> rest;
    public Associativity associativity;
    public String label;

    public Seq() { }

    public Seq(Symbol symbol, List<Symbol> rest, Associativity associativity, String label) {
        this.first = symbol;
        this.rest = rest;
        this.associativity = associativity;
        this.label = label;
    }

    public List<Symbol> getSymbols() {
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(first);
        if (rest != null) {
            symbols.addAll(rest);
        }
        return symbols;
    }
}
