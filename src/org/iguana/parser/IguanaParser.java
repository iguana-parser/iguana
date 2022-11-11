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

import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parser.options.ParseOptions;
import org.iguana.parser.options.ParseTreeOptions;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.traversal.AmbiguousSPPFToParseTreeVisitor;
import org.iguana.traversal.DefaultSPPFToParseTreeVisitor;
import org.iguana.util.Configuration;
import org.iguana.utils.input.Input;

import static org.iguana.parser.options.ParseOptions.defaultOptions;

public class IguanaParser extends IguanaRecognizer {

    private static final ParserResultOps parserResultOps = new ParserResultOps();

    private ParseTreeNode parseTree;
    private NonterminalNode sppf;
    private Input input;

    public IguanaParser(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaParser(Grammar grammar, Configuration config) {
        super(grammar, config);
    }

    public IguanaParser(RuntimeGrammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaParser(RuntimeGrammar grammar, Configuration config) {
        super(grammar, config);
    }

    public void parse(Input input, Symbol symbol) {
        if (symbol instanceof Nonterminal) parse(input, (Nonterminal) symbol);
        else if (symbol instanceof Start) parse(input, (Start) symbol);
        else throw new RuntimeException("Symbol should be a nonterminal or start, but was: " + symbol.getClass());
    }

    public void parse(Input input, Start start) {
        parse(input, Nonterminal.withName(assertStartSymbolNotNull(start).getName()), defaultOptions());
    }

    public void parse(Input input, Nonterminal nonterminal) {
        parse(input, nonterminal, defaultOptions());
    }

    public void parse(Input input, Nonterminal start, ParseOptions parseOptions) {
        clear();
        this.input = input;
        IguanaRuntime<NonPackedNode> runtime = new IguanaRuntime<>(config, parserResultOps);
        long startTime = System.nanoTime();
        this.sppf = (NonterminalNode) runtime.run(input, start, grammarGraph, parseOptions.getMap(),
            parseOptions.isGlobal());
        long endTime = System.nanoTime();
        this.statistics = runtime.getStatistics();
        if (sppf == null) {
            this.parseError = runtime.getParseError();
            throw new ParseErrorException(parseError);
        } else {
            System.out.println("Parsing finished in " + (endTime - startTime) / 1000_000 + "ms.");
        }
    }

    @Override
    protected void clear() {
        super.clear();
        this.sppf = null;
        this.parseTree = null;
        this.input = null;
    }

    public NonterminalNode getSPPF() {
        return sppf;
    }

    public ParseTreeNode getParseTree() {
        return getParseTree(ParseTreeOptions.defaultOptions());
    }

    public ParseTreeNode getParseTree(ParseTreeOptions options) {
        if (parseTree != null) return parseTree;

        if (sppf == null) return null;

        boolean allowAmbiguities = options.allowAmbiguities();
        boolean ignoreLayout = options.ignoreLayout();

        if (allowAmbiguities) {
            AmbiguousSPPFToParseTreeVisitor<ParseTreeNode> visitor =
                new AmbiguousSPPFToParseTreeVisitor<>(getParseTreeBuilder(input), ignoreLayout, parserResultOps);
            long start = System.nanoTime();
            ParseTreeNode node = (ParseTreeNode) sppf.accept(visitor).getValues().get(0);
            long end = System.nanoTime();
            System.out.println("Parse tree creation finished in " + (end - start) / 1000_000 + "ms.");
            return node;
        }

        DefaultSPPFToParseTreeVisitor<ParseTreeNode> visitor =
            new DefaultSPPFToParseTreeVisitor<>(getParseTreeBuilder(input), input, ignoreLayout, parserResultOps);
        long start = System.nanoTime();
        this.parseTree = sppf.accept(visitor);
        long end = System.nanoTime();
        System.out.println("Parse tree creation finished in " + (end - start) / 1000_000 + "ms.");

        return parseTree;
    }

    public ParseStatistics getStatistics() {
        return (ParseStatistics) statistics;
    }

    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {
        return new DefaultParseTreeBuilder(input);
    }
}
