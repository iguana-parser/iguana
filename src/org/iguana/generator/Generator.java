package org.iguana.generator;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    /**
     *
     * @param f a function from the nonterminal name to the generated string
     */
    protected void generateTypes(StringBuilder sb, Function<String, String> f) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append(f.apply(nonterminalName));
            } else if (alternatives.size() == 1) {
                sb.append(f.apply(nonterminalName));
            } else {
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() +
                                      nonterminalName.substring(1);
                    sb.append(f.apply(nodeName));
                }
            }
        }
    }
}
