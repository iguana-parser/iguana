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
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.ErrorTransition;
import org.iguana.grammar.slot.GrammarSlotUtil;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.gss.GSSEdge;
import org.iguana.gss.GSSNode;
import org.iguana.parser.options.ParseOptions;
import org.iguana.parser.options.ParseTreeOptions;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.ErrorNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.traversal.AmbiguousSPPFToParseTreeVisitor;
import org.iguana.traversal.DefaultSPPFToParseTreeVisitor;
import org.iguana.util.Configuration;
import org.iguana.util.Tuple;
import org.iguana.utils.input.Input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

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

    public void parse(Input input, Symbol symbol, ParseOptions parseOptions) {
        if (symbol instanceof Nonterminal) parse(input, (Nonterminal) symbol, parseOptions);
        else if (symbol instanceof Start) parse(input, (Start) symbol, parseOptions);
        else throw new RuntimeException("Symbol should be a nonterminal or start, but was: " + symbol.getClass());
    }

    public void parse(Input input, Symbol symbol) {
        parse(input, symbol, ParseOptions.defaultOptions());
    }

    public void parse(Input input, Start start) {
        parse(input, Nonterminal.withName(assertStartSymbolNotNull(start).getName()), defaultOptions());
    }

    public void parse(Input input, Start start, ParseOptions parseOptions) {
        parse(input, Nonterminal.withName(assertStartSymbolNotNull(start).getName()), parseOptions);
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
            if (parseOptions.isErrorRecoveryEnabled()) {
                PriorityQueue<ParseError<NonPackedNode>> parseErrors = runtime.getParseErrors();
                outer:
                while (!parseErrors.isEmpty()) {
                    List<Tuple<GSSEdge<NonPackedNode>, ErrorTransition>> errorSlots = new ArrayList<>();
                    GSSNode<NonPackedNode> gssNode = parseErrors.poll().getGssNode();
                    runtime.collectErrorSlots(gssNode, errorSlots, new HashSet<>());
                    for (Tuple<GSSEdge<NonPackedNode>, ErrorTransition> t : errorSlots) {
                        runtime.recoverFromError(t.getFirst(), t.getSecond(), input);
                        NonPackedNode recoveryResult = runtime.runParserLoop(runtime.getStartGSSNode(), input);
                        if (recoveryResult != null) {
                            sppf = (NonterminalNode) recoveryResult;
                            break outer;
                        }
                    }
                }
            }
            if (sppf == null) {
                this.parseError = runtime.getParseErrors().peek();
                if (parseOptions.isErrorRecoveryEnabled()) {
                    NonterminalGrammarSlot startSlot = grammarGraph.getStartSlot(start);
                    if (!startSlot.getFirstSlots().isEmpty()) {
                        BodyGrammarSlot firstSlot = startSlot.getFirstSlots().get(0);
                        EndGrammarSlot endSlot = GrammarSlotUtil.getEndSlot(firstSlot);
                        int inputLength = input.length() - 1;
                        ErrorNode errorNode = new ErrorNode(endSlot, 0, inputLength);
                        sppf = new NonterminalNode(endSlot, errorNode, 0, inputLength);
                    }
                } else {
                    throw new ParseErrorException(parseError);
                }
            } else {
                System.out.println("Parsing finished with error recovery " + (endTime - startTime) / 1000_000 + "ms.");
            }
        } else {
            System.out.println("Parsing finished successfully in " + (endTime - startTime) / 1000_000 + "ms.");
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
