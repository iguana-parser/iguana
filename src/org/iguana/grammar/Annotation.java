package org.iguana.grammar;

import org.iguana.grammar.symbol.Symbol;

import java.util.List;

public class Annotation {

    private final String name;
    private final List<String> values;
    private final Symbol symbol;

    public Annotation(String name, List<String> values, Symbol symbol) {
        this.name = name;
        this.values = values;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
