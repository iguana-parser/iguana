package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

import java.util.HashMap;
import java.util.Map;

public class IguanaRecognizer {

    private static final RecognizerResultOps recognizerResultOps = new RecognizerResultOps();

    protected final RuntimeGrammar grammar;
    protected final Configuration config;
    protected final Map<String, Object> globals;

    // A map from the start symbol to the grammar graph. We cache the grammar graphs as they are expensive
    // to build. This is helpful in parsing multiple inputs with the same parser instance.
    protected final Map<String, GrammarGraph> grammarGraphs;

    protected ParseError parseError;
    protected RecognizerStatistics statistics;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Configuration config) {
        this.grammar = grammar;
        this.grammarGraphs = new HashMap<>();
        this.config = config;
        this.globals = grammar.getGlobals();
    }

    public IguanaRecognizer(Grammar grammar, Configuration config) {
        this(grammar.toRuntimeGrammar(), config);
    }

    public boolean recognize(Input input) {
        return recognize(input, grammar.getStartSymbol().getStartSymbol());
    }

    public boolean recognize(Input input, String startNonterminal) {
        return recognize(input, startNonterminal, globals, false);
    }

    public boolean recognize(Input input, String startNonterminal, Map<String, Object> map, boolean global) {
        IguanaRuntime<RecognizerResult> runtime = new IguanaRuntime<>(config, recognizerResultOps);
        GrammarGraph grammarGraph = createGrammarGraph(startNonterminal);
        RecognizerResult root = (RecognizerResult) runtime.run(input, grammarGraph, map, global);
        this.parseError = runtime.getParseError();
        this.statistics = runtime.getStatistics();
        if (root == null) return false;
        return root.getIndex() == input.length() - 1;
    }

    public RecognizerStatistics getStatistics() {
        return statistics;
    }

    public ParseError getParseError() {
        return parseError;
    }

    protected void clear() {
        parseError = null;
        statistics = null;
    }

    protected GrammarGraph createGrammarGraph(String startNonterminal) {
        return grammarGraphs.computeIfAbsent(
            startNonterminal,
            key -> GrammarGraphBuilder.from(GrammarTransformer.transform(grammar, startNonterminal), config));
    }
}
