/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.NonterminalNode;
import org.iguana.traversal.AmbiguousSPPFToParseTreeVisitor;
import org.iguana.traversal.DefaultSPPFToParseTreeVisitor;
import org.iguana.util.Configuration;

public class IguanaParser {

    private final GrammarGraph grammarGraph;
    private final IguanaRuntime runtime;

    public IguanaParser(RuntimeGrammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaParser(RuntimeGrammar grammar, Configuration config) {
        this.grammarGraph = GrammarGraphBuilder.from(grammar, config);
        this.runtime = new IguanaRuntime<>(config, new ParserResultOps());
    }

    public NonterminalNode getSPPF(Input input) {
        return getSPPF(input, new ParseOptions.Builder().build());
    }

    public NonterminalNode getSPPF(Input input, ParseOptions options) {
        return (NonterminalNode) runtime.run(input, grammarGraph, options.getMap(), options.isGlobal());
    }

    public ParseTreeNode getParserTree(Input input) {
        return getParserTree(input, new ParseOptions.Builder().build());
    }

    public ParseTreeNode getParserTree(Input input, ParseOptions options) {
        NonterminalNode root = getSPPF(input, options);

        if (root == null) {
            return null;
        }

        if (options.ambiguous()) {
            AmbiguousSPPFToParseTreeVisitor<ParseTreeNode> visitor = new AmbiguousSPPFToParseTreeVisitor<>(new DefaultParseTreeBuilder(input), options.ignoreLayout(), (ParserResultOps) runtime.getResultOps());
            return (ParseTreeNode) root.accept(visitor).getValues().get(0);
        }

        DefaultSPPFToParseTreeVisitor converter = new DefaultSPPFToParseTreeVisitor<>(new DefaultParseTreeBuilder(input), input, options.ignoreLayout());
        return (ParseTreeNode) converter.convertNonterminalNode(root);
    }

    public GrammarGraph getGrammarGraph() {
        return grammarGraph;
    }

    public ParseError getParseError() {
        return runtime.getParseError();
    }

    public ParseStatistics getStatistics() {
        return (ParseStatistics) runtime.getStatistics();
    }

}
