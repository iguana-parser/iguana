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
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.gss.GSSNode;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.RecognizerResult;
import org.iguana.result.Result;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.iguana.util.ParserLogger;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class Iguana {

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar) {
        if (grammar.getStartSymbol() == null)
            throw new IllegalArgumentException("No start symbol defined in the grammar");

        return parse(input, grammar, Nonterminal.withName(grammar.getStartSymbol().getName()));
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol) {
        return parse(input, grammar, startSymbol, Configuration.load());
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config) {
        return parse(input, grammar, startSymbol, config, Collections.emptyMap());
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map) {
        return parse(input, grammar, startSymbol, config, map, true);
    }

    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Map<String, ?> map, boolean global) {
        return parse(input, grammar, startSymbol, Configuration.load(), map, global);
    }


    public static ParseResult<NonPackedNode> parse(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map, boolean global) {
        GrammarGraph grammarGraph = GrammarGraph.from(grammar, config);
        return run(input, new ParserRuntime(config), grammarGraph, startSymbol, map, global);
    }

    public static ParseResult<RecognizerResult> recognize(Input input, Grammar grammar) {
        if (grammar.getStartSymbol() == null)
            throw new IllegalArgumentException("No start symbol defined in the grammar");

        return recognize(input, grammar, Nonterminal.withName(grammar.getStartSymbol().getName()));
    }

    public static ParseResult<RecognizerResult> recognize(Input input, Grammar grammar, Nonterminal startSymbol) {
        return recognize(input, grammar, startSymbol, Configuration.load());
    }

    public static ParseResult<RecognizerResult> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config) {
        return recognize(input, grammar, startSymbol, config, Collections.emptyMap());
    }

    public static ParseResult<RecognizerResult> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map) {
        return recognize(input, grammar, startSymbol, config, map, true);
    }

    public static ParseResult<RecognizerResult> recognize(Input input, Grammar grammar, Nonterminal startSymbol, Configuration config, Map<String, ?> map, boolean global) {
        GrammarGraph grammarGraph = GrammarGraph.from(grammar, config);
        return run(input, new RecognizerRuntime(config), grammarGraph, startSymbol, map, global);
    }

    public static <T extends Result> ParseResult<T> run(Input input, Runtime<T> runtime, GrammarGraph grammarGraph, Nonterminal nonterminal, Map<String, ?> map, boolean global) {
        grammarGraph.reset(input);

        IEvaluatorContext ctx = runtime.getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        NonterminalGrammarSlot startSymbol = grammarGraph.getHead(nonterminal);

        if (startSymbol == null) {
            throw new RuntimeException("No nonterminal named " + nonterminal + " found");
        }

        Result root;

        Environment env = ctx.getEmptyEnvironment();

        GSSNode<T> startGSSNode;

        if (!global && !map.isEmpty()) {
            Object[] arguments = new Object[map.size()];

            int i = 0;
            for (String parameter : nonterminal.getParameters())
                arguments[i++] = map.get(parameter);

            startGSSNode = startSymbol.getGSSNode(0, arguments);
            env = ctx.getEmptyEnvironment().declare(nonterminal.getParameters(), arguments);
        } else {
            startGSSNode = startSymbol.getGSSNode(0);
        }

        ParserLogger logger = ParserLogger.getInstance();
        logger.reset();

        Timer timer = new Timer();
        timer.start();

        for (BodyGrammarSlot slot : startSymbol.getFirstSlots()) {
            runtime.scheduleDescriptor(slot, startGSSNode, runtime.getResultOps().dummy(), env);
        }

        while(runtime.hasDescriptor()) {
            Descriptor<T> descriptor = runtime.nextDescriptor();
            logger.processDescriptor(descriptor);
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(), descriptor.getEnv(), runtime);
        }

        root = startGSSNode.getResult(input.length() - 1);

        timer.stop();

        if (root == null) {
            ParseError error = runtime.getParseError();
            logger.error(error.getSlot(), error.getInputIndex());
            return error;
        } else {
            ParseStatistics parseStatistics = runtime.getParseStatistics(timer);
            return new ParseSuccess(root, parseStatistics);
        }
    }

    private static  void printStats(GrammarGraph grammarGraph) {
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

    private static  double[] stats(Iterable<GSSNode> gssNodes, Function<GSSNode, Integer> f) {
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

        return new double[] { min, max, (double) sum / count };
    }
}
