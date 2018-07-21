package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.datadependent.env.EnvironmentPool;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class IguanaRecognizer {

    private final GrammarGraph grammarGraph;
    private final Configuration config;
    private final IguanaRuntime runtime;
    private final Grammar grammar;

    private RecognizerResult root;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(Grammar grammar, Configuration config) {
        this.grammar = grammar;
        this.grammarGraph = GrammarGraph.from(grammar, config);
        this.config = config;
        this.runtime = new IguanaRuntime(config, new RecognizerResultOps());
    }

    public boolean recognize(Input input) {
        return recognize(input, Nonterminal.withName(grammar.getStartSymbol().getName()), emptyMap(), true);
    }

    public boolean recognize(Input input, Nonterminal nonterminal, Map<String, Object> map, boolean global) {
        EnvironmentPool.clean();
        grammarGraph.reset(input);
        root = (RecognizerResult) runtime.run(input, grammarGraph, nonterminal, map, global);
        return root.getIndex() == input.length() - 1;
    }

    public ParseStatistics getStatistics() {
        return runtime.getParseStatistics();
    }

    public ParseError getParseError() {
        return runtime.getParseError();
    }
}
