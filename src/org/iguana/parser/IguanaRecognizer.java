package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.transformation.*;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

import java.util.Map;

public class IguanaRecognizer {

    protected final GrammarGraph grammarGraph;
    protected final Configuration config;
    protected final Map<String, Object> globals;

    protected ParseError parseError;
    protected RecognizerStatistics statistics;

    public IguanaRecognizer(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaRecognizer(RuntimeGrammar grammar, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(transform(grammar), config);
        this.config = config;
        this.globals = grammar.getGlobals();
    }

    public IguanaRecognizer(Grammar grammar, Configuration config) {
       this(grammar.toRuntimeGrammar(), config);
    }

    public boolean recognize(Input input) {
        return recognize(input, globals, false);
    }

    public boolean recognize(Input input, Map<String, Object> map, boolean global) {
        clear();
        IguanaRuntime<RecognizerResult> runtime = new IguanaRuntime<>(config, new RecognizerResultOps());
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

    public GrammarGraph getGrammarGraph() {
        return grammarGraph;
    }

    protected void clear() {
        grammarGraph.clear();
        parseError = null;
        statistics = null;
    }

    public static RuntimeGrammar transform(RuntimeGrammar runtimeGrammar) {
        RuntimeGrammar grammar = new ResolveIdentifiers().transform(runtimeGrammar);
        DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
        desugarAlignAndOffside.doAlign();
        grammar = desugarAlignAndOffside.transform(grammar);
        grammar = new EBNFToBNF().transform(grammar);
        desugarAlignAndOffside.doOffside();
        grammar = desugarAlignAndOffside.transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        grammar = new DesugarState().transform(grammar);
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        return grammar;
    }
}
