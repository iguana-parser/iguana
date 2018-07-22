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
import org.iguana.datadependent.env.EnvironmentPool;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.gss.GSSNode;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.NonterminalNode;
import org.iguana.traversal.AmbiguousSPPFToParseTreeVisitor;
import org.iguana.traversal.DefaultSPPFToParseTreeVisitor;
import org.iguana.util.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

public class IguanaParser {

    private final Grammar grammar;
    private final GrammarGraph grammarGraph;
    private final IguanaRuntime runtime;

    private NonterminalNode root;

    public IguanaParser(Grammar grammar) {
        this(grammar, Configuration.load());
    }

    public IguanaParser(Grammar grammar, Configuration config) {
        this.grammar = grammar;
        this.grammarGraph = GrammarGraph.from(grammar, config);
        this.runtime = new IguanaRuntime<>(config, new ParserResultOps());
    }

    public boolean parse(Input input) {
        return parse(input, Nonterminal.withName(grammar.getStartSymbol().getName()), emptyMap(), true);
    }

    public boolean parse(Input input, Nonterminal nonterminal) {
        return parse(input, nonterminal, emptyMap(), true);
    }

    public boolean parse(Input input, Nonterminal nonterminal, Map<String, Object> map, boolean global) {
        EnvironmentPool.clean();
        grammarGraph.reset(input);
        root = (NonterminalNode) runtime.run(input, grammarGraph, nonterminal, map, global);
        return root != null;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public GrammarGraph getGrammarGraph() {
        return grammarGraph;
    }

    public ParseError getParseError() {
        return runtime.getParseError();
    }

    public ParseStatistics getStatistics() {
        return runtime.getParseStatistics();
    }

    public NonterminalNode getSPPF() {
        return root;
    }

    public ParseTreeNode getParseTree() {
        return getParseTree(emptySet(), false);
    }

    public ParseTreeNode getParseTree(boolean ambiguous) {
        return getParseTree(emptySet(), ambiguous);
    }

    public ParseTreeNode getParseTree(Set<String> ignoreSet) {
        return getParseTree(ignoreSet, false);
    }

    public ParseTreeNode getParseTree(Set<String> ignoreSet, boolean ambiguous) {
        if (ambiguous) {
            AmbiguousSPPFToParseTreeVisitor<ParseTreeNode> visitor = new AmbiguousSPPFToParseTreeVisitor<>(new DefaultParseTreeBuilder(), ignoreSet);
            return (ParseTreeNode) root.accept(visitor).getValues().get(0);
        }
        DefaultSPPFToParseTreeVisitor converter = new DefaultSPPFToParseTreeVisitor<>(new DefaultParseTreeBuilder(), ignoreSet);
        return (ParseTreeNode) converter.convert(root);
    }

    private static void printStats(GrammarGraph grammarGraph) {
        for (TerminalGrammarSlot slot : grammarGraph.getTerminalGrammarSlots()) {
            System.out.println(slot.getTerminal().getName() + " : " + slot.countTerminalNodes());
        }

        for (NonterminalGrammarSlot slot : grammarGraph.getNonterminalGrammarSlots()) {
            System.out.print(slot.getNonterminal().getName());
            System.out.println(" GSS nodes: " + slot.countGSSNodes());
            double[] poppedElementStats = stats(slot.getGSSNodes(), GSSNode::countPoppedElements);
            double[] gssEdgesStats = stats(slot.getGSSNodes(), GSSNode::countGSSEdges);
            if (poppedElementStats == null)
                System.out.println("Popped Elements: empty");
            else
                System.out.printf("Popped Elements (min: %d, max: %d, mean: %.2f)%n", (int) poppedElementStats[0], (int) poppedElementStats[1], poppedElementStats[2]);

            if (gssEdgesStats == null)
                System.out.println("GSS Edges: empty");
            else
                System.out.printf("GSS Edges (min: %d, max: %d, mean: %.2f)%n", (int) gssEdgesStats[0], (int) gssEdgesStats[1], gssEdgesStats[2]);
            System.out.println("---------------");
        }
    }

    private static double[] stats(Iterable<GSSNode> gssNodes, Function<GSSNode, Integer> f) {
        if (!gssNodes.iterator().hasNext()) return null;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int sum = 0;
        int count = 0;

        for (GSSNode gssNode : gssNodes) {
            min = Integer.min(min, f.apply(gssNode));
            max = Integer.max(max, f.apply(gssNode));
            sum += f.apply(gssNode);
            count++;
        }

        return new double[]{min, max, (double) sum / count};
    }
}
