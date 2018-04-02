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

import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.descriptor.IntResultOps;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.parser.descriptor.SPPFResultOps;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.iguana.util.ParserLogger;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class Iguana {

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar) {
        if (grammar.getStartSymbol() == null)
            throw new IllegalArgumentException("No start symbol defined in the grammar");

        return parse(input, grammar, Nonterminal.withName(grammar.getStartSymbol().getName()));
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol) {
        return parse(input, grammar, startSymbol, Configuration.DEFAULT);
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config) {
        return parse(input, grammar, startSymbol, config, Collections.emptyMap());
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map) {
        return parse(input, grammar, startSymbol, config, map, true);
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map, boolean global) {
        GrammarGraph<NonPackedNode> grammarGraph = GrammarGraph.from(grammar, input, config, new SPPFResultOps());
        return parse(input, grammarGraph, startSymbol, config, map, global);
    }

    public static ParseResult<Integer> recognize(Input input, Grammar grammar) {
        if (grammar.getStartSymbol() == null)
            throw new IllegalArgumentException("No start symbol defined in the grammar");

        return recognize(input, grammar, Nonterminal.withName(grammar.getStartSymbol().getName()));
    }

    public static ParseResult<Integer> recognize(Input input, Grammar grammar, Nonterminal startSymbol) {
        return recognize(input, grammar, startSymbol, Configuration.DEFAULT);
    }

    public static ParseResult<Integer> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config) {
        return recognize(input, grammar, startSymbol, config, Collections.emptyMap());
    }

    public static ParseResult<Integer> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map) {
        return recognize(input, grammar, startSymbol, config, map, true);
    }

    public static ParseResult<Integer> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map, boolean global) {
        GrammarGraph<Integer> grammarGraph = GrammarGraph.from(grammar, input, config, new IntResultOps());
        return parse(input, grammarGraph, startSymbol, config, map, global);
    }

    public static <T> ParseResult<T> parse(Input input, GrammarGraph<T> grammarGraph, Nonterminal nonterminal, Configuration config, Map<String, ?> map, boolean global) {
        IEvaluatorContext ctx = GLLEvaluator.getEvaluatorContext(config, input);

        ParserRuntime<T> runtime = grammarGraph.getRuntime();

        grammarGraph.reset(input);

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        NonterminalGrammarSlot<T> startSymbol = grammarGraph.getHead(nonterminal);

        if (startSymbol == null) {
            throw new RuntimeException("No nonterminal named " + nonterminal + " found");
        }

        T root;

        final Environment env;

        GSSNode<T> startGSSNode;

        if (!global && !map.isEmpty()) {
            Object[] arguments = new Object[map.size()];

            int i = 0;
            for (String parameter : nonterminal.getParameters())
                arguments[i++] = map.get(parameter);

            startGSSNode = startSymbol.getGSSNode(0, new GSSNodeData<>(arguments));
            env = ctx.getEmptyEnvironment().declare(nonterminal.getParameters(), arguments);
        } else {
            env = null;
            startGSSNode = startSymbol.getGSSNode(0);
        }

        ParserLogger logger = ParserLogger.getInstance();
        logger.reset();

        logger.log("Parsing %s:", input.getURI());

        Timer timer = new Timer();
        timer.start();

        ResultOps<T> ops = grammarGraph.getResultOps();

        if (env == null)
            startSymbol.getFirstSlots().forEach(s -> runtime.scheduleDescriptor(s, startGSSNode, ops.dummy(0)));
        else
            startSymbol.getFirstSlots().forEach(s -> runtime.scheduleDescriptor(s, startGSSNode, ops.dummy(0), env));

        while(runtime.hasDescriptor()) {
            Descriptor descriptor = runtime.nextDescriptor();
            logger.log("Processing %s", descriptor);
            descriptor.execute(input);
        }

        root = startGSSNode.getResult(input.length() - 1);

        timer.stop();

        ParseResult<T> parseResult;

        if (root == null) {
            parseResult = runtime.getParseError();
            logger.log("Parse error:\n %s", parseResult);
        } else {
            ParseStatistics parseStatistics = runtime.getParseStatistics(timer);
            parseResult = new ParseSuccess<>(root, parseStatistics, input);
            logger.log("Parsing finished successfully.");
            logger.log(parseStatistics.toString());
        }

        return parseResult;
    }

    private static <T> void printStats(GrammarGraph<T> grammarGraph) {
        for (NonterminalGrammarSlot<T> slot : grammarGraph.getNonterminalGrammarSlots()) {
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

    private static <T> double[] stats(Iterable<GSSNode<T>> gssNodes, Function<GSSNode<T>, Integer> f) {
        if (!gssNodes.iterator().hasNext()) return null;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int sum = 0;
        int count = 0;

        for (GSSNode<T> gssNode : gssNodes) {
            min = Integer.min(min, f.apply(gssNode));
            max = Integer.max(max, f.apply(gssNode));
            sum += f.apply(gssNode);
            count++;
        }

        return new double[] { min, max, (double) sum / count };
    }
}
