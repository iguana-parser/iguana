package org.iguana.grammar.transformation;

import org.iguana.grammar.symbol.Symbol;

import java.util.function.Function;
import java.util.function.Predicate;

public class SymbolTransformation {

    private final Predicate<Symbol> predicate;
    private final Function<Symbol, Symbol> transformer;

    public SymbolTransformation(Predicate<Symbol> predicate, Function<Symbol, Symbol> transformer) {
        this.predicate = predicate;
        this.transformer = transformer;
    }

    public boolean isDefinedAt(Symbol symbol) {
        return predicate.test(symbol);
    }

    public Symbol apply(Symbol symbol) {
        return transformer.apply(symbol);
    }
}
