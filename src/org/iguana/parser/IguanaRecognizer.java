package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class IguanaRecognizer {

    private final GrammarGraph grammarGraph;
    private final IguanaRuntime runtime;

    public IguanaRecognizer(RuntimeGrammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(grammar, config);
        this.runtime = new IguanaRuntime<>(config, new RecognizerResultOps());
    }

    public boolean recognize(Input input) {
        return recognize(input, emptyMap(), true);
    }

    public boolean recognize(Input input, Map<String, Object> map, boolean global) {
        RecognizerResult root = (RecognizerResult) runtime.run(input, grammarGraph, map, global);
        if (root == null) return false;
        return root.getIndex() == input.length() - 1;
    }

    public RecognizerStatistics getStatistics() {
        return runtime.getStatistics();
    }

    public ParseError getParseError() {
        return runtime.getParseError();
    }
}
