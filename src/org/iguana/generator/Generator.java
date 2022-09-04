package org.iguana.generator;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Symbol;

public abstract class Generator {

    protected final RuntimeGrammar grammar;
    protected final String grammarName;
    protected final String packageName;
    protected final String genDirectory;

    public Generator(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        this.grammar = grammar;
        this.grammarName = grammarName;
        this.packageName = packageName;
        this.genDirectory = genDirectory;
    }

    protected String symbolToString(Symbol symbol) {
        String label = getLabel(symbol);
        return (label == null ? "" : label + ":") + symbol.getName();
    }

    protected String getLabel(Symbol symbol) {
        if (symbol instanceof Code) {
            if (symbol.getLabel() != null) return symbol.getLabel();
            return ((Code) symbol).getSymbol().getLabel();
        }
        return symbol.getLabel();
    }
}
