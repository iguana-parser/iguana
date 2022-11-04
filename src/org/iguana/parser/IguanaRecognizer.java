package org.iguana.parser;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;
import org.iguana.utils.input.Input;

import java.util.Collections;
import java.util.Map;

public class IguanaRecognizer {

    private static final RecognizerResultOps recognizerResultOps = new RecognizerResultOps();

    protected final GrammarGraph grammarGraph;
    protected final Configuration config;

    protected ParseError<?> parseError;
    protected RecognizerStatistics statistics;

    protected final RuntimeGrammar finalGrammar;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(Grammar grammar, Configuration config) {
        this(GrammarTransformer.transform(grammar.toRuntimeGrammar()), config);
    }

    public IguanaRecognizer(RuntimeGrammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(grammar, config);
        this.config = config;
        this.finalGrammar = grammar;
    }

    public boolean recognize(Input input, Symbol symbol) {
        if (symbol instanceof Nonterminal) return recognize(input, (Nonterminal) symbol);
        else if (symbol instanceof Start) return recognize(input, (Start) symbol);
        else throw new RuntimeException("Symbol should be a nonterminal or start, but was: " + symbol.getClass());
    }

    public boolean recognize(Input input, Nonterminal nonterminal) {
        return recognize(input, nonterminal, Collections.emptyMap(), false);
    }

    public boolean recognize(Input input, Start start) {
        return recognize(input, Nonterminal.withName(assertStartSymbolNotNull(start).getName()), Collections.emptyMap(),
            false);
    }

    public boolean recognize(Input input, Nonterminal start, Map<String, Object> map, boolean global) {
        clear();
        IguanaRuntime<RecognizerResult> runtime = new IguanaRuntime<>(config, recognizerResultOps);
        RecognizerResult result = runtime.run(input, start, grammarGraph, map, global);
        this.statistics = runtime.getStatistics();
        if (result == null) {
            this.parseError = runtime.getParseError();
            return false;
        }
        return true;
    }

    public RecognizerStatistics getStatistics() {
        return statistics;
    }

    public ParseError<?> getParseError() {
        return parseError;
    }

    public RuntimeGrammar getFinalGrammar() {
        return finalGrammar;
    }

    protected void clear() {
        grammarGraph.clear();
        parseError = null;
        statistics = null;
    }

    protected static Start assertStartSymbolNotNull(Start start) {
        if (start == null) {
            throw new RuntimeException("Start symbol is not set");
        }
        return start;
    }
}
