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

package org.iguana.parser.basic;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.RuleNode;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import java.util.Collections;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static org.iguana.util.CollectionsUtil.set;
import static org.junit.Assert.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;


/**
 * 
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 * 
 */
public class Test2 {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Character a = Character.from('a');
    static Rule r1 = Rule.withHead(A).addSymbol(a).build();

    private static Input input = Input.fromString("a");
    private static Grammar grammar;
	private static Nonterminal startSymbol = A;

	static {
		grammar = Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(A));
	}

    @Test
    public void testParser() {
        GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult(graph), result);
        assertEquals(getTree(), result.asParseSuccess().getTree());
    }
	
	private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(1)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(0)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(1)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0);
        return node1;
	}

    public static RuleNode getTree() {
        return createRule(r1, list(createTerminal("a")));
    }

}
