package org.iguana.parser;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
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

    protected ParseError parseError;
    protected RecognizerStatistics statistics;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, Nonterminal.withName(assertStartSymbolNotNull(grammar.getStartSymbol()).getName()), Configuration.load());
    }

    public IguanaRecognizer(Grammar grammar, Nonterminal start, Configuration config) {
        this(GrammarTransformer.transform(grammar.toRuntimeGrammar()), start, config);
    }

    public IguanaRecognizer(RuntimeGrammar grammar) {
        this(grammar, Nonterminal.withName(assertStartSymbolNotNull(grammar.getStartSymbol()).getName()), Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Nonterminal start, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(grammar, start, config);
        this.config = config;
    }

    public boolean recognize(Input input) {
        return recognize(input, Collections.emptyMap(), false);
    }

    public boolean recognize(Input input,  Map<String, Object> map, boolean global) {
        clear();
        IguanaRuntime<RecognizerResult> runtime = new IguanaRuntime<>(config, recognizerResultOps);
        RecognizerResult root = (RecognizerResult) runtime.run(input, grammarGraph, map, global);
        this.parseError = runtime.getParseError();
        this.statistics = runtime.getStatistics();
        if (root == null) return false;
        return root.getRightExtent() == input.length() - 1;
    }

    public RecognizerStatistics getStatistics() {
        return statistics;
    }

    public ParseError getParseError() {
        return parseError;
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
