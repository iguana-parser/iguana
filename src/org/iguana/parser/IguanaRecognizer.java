package org.iguana.parser;

import org.iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

import java.util.Collections;
import java.util.Map;

public class IguanaRecognizer {

    private static final RecognizerResultOps recognizerResultOps = new RecognizerResultOps();

    protected final GrammarGraph grammarGraph;
    protected final Configuration config;

    protected ParseError parseError;
    protected RecognizerStatistics statistics;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, grammar.getStartSymbol().getStartSymbol(), Configuration.load());
    }

    public IguanaRecognizer(Grammar grammar, String startNonterminal, Configuration config) {
        this(GrammarTransformer.transform(grammar.toRuntimeGrammar(), startNonterminal), config);
    }

    public IguanaRecognizer(RuntimeGrammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(grammar, config);
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

    public boolean hasParseError() {
        return parseError != null;
    }

    protected void clear() {
        grammarGraph.clear();
        parseError = null;
        statistics = null;
    }
}
