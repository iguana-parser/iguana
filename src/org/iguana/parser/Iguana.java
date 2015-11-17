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

import iguana.parsetrees.sppf.DummyNode;
import iguana.parsetrees.sppf.NonterminalNode;
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
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class Iguana {
	
	public static ParseResult parse(Input input, GrammarGraph grammarGraph, Configuration config, Nonterminal nonterminal, Map<String, ?> map, boolean global) {
        IEvaluatorContext ctx = GLLEvaluator.getEvaluatorContext(config, input);

        ParserRuntime runtime = grammarGraph.getRuntime();

        grammarGraph.reset(input);

        if (global)
            map.forEach((k,v) -> ctx.declareGlobalVariable(k, v));

        NonterminalGrammarSlot startSymbol = grammarGraph.getHead(nonterminal);

        if(startSymbol == null) {
            throw new RuntimeException("No nonterminal named " + nonterminal + " found");
        }

        NonterminalNode root;

        final Environment env;

        GSSNode startGSSNode;

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

        runtime.log("Parsing %s:", input.getURI());

        Timer timer = new Timer();
        timer.start();

        if (env == null)
            startSymbol.getFirstSlots().forEach(s -> runtime.scheduleDescriptor(new Descriptor(s, startGSSNode, new DummyNode(0), input)));
        else
            startSymbol.getFirstSlots().forEach(s -> runtime.scheduleDescriptor(new org.iguana.datadependent.descriptor.Descriptor(s, startGSSNode, new DummyNode(0), input, env)));

        while(runtime.hasDescriptor()) {
            Descriptor descriptor = runtime.nextDescriptor();
            runtime.log("Processing %s", descriptor);
            descriptor.execute();
        }

        root = startGSSNode.getNonterminalNode(input, input.length() - 1);

        timer.stop();

        ParseResult parseResult;

        if (root == null) {
            parseResult = runtime.getParseError();
            runtime.log("Parse error:\n %s", parseResult);
        } else {
            ParseStatistics parseStatistics = runtime.getParseStatistics(timer);
            parseResult = new ParseSuccess(root, parseStatistics, input);
            runtime.log("Parsing finished successfully.");
            runtime.log(parseStatistics.toString());
        }

        return parseResult;
    }
	
	public static ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol) {
		return parse(input, grammarGraph, Configuration.load(), startSymbol, Collections.emptyMap(), true);
	}

    public static ParseResult parse(Input input, Grammar grammar, Nonterminal startSymbol) {
        return parse(input, GrammarGraph.from(grammar, input, Configuration.load()), startSymbol);
    }
	
	public static ParseResult parse(Input input, Grammar grammar, Configuration config, Nonterminal startSymbol) {
		return parse(input, GrammarGraph.from(grammar, input, config), startSymbol);
	}
	
	public static ParseResult parse(Input input, Grammar grammar, Configuration config, Nonterminal startSymbol, Map<String, ?> map) {
		return parse(input, GrammarGraph.from(grammar, input, config), config, startSymbol, map, true);
	}
}
