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

import static org.iguana.util.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
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

import iguana.utils.input.Input;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;


/**
 * 
 * A ::= a A b
 *	   | a A c
 *     | a
 *     
 * @author Ali Afroozeh
 * 
 */
public class Test17 {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c  = Character.from('c');

    public static Grammar grammar;
    private static Input input = Input.fromString("aaabb");
    private static Nonterminal startSymbol = A;

    static {
        Rule r1 = Rule.withHead(A).addSymbols(a, A, b).build();
        Rule r2 = Rule.withHead(A).addSymbols(a, A, c).build();
        Rule r3 = Rule.withHead(A).addSymbols(a).build();
        grammar = Grammar.builder().addRules(r1, r2, r3).build();
    }

	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(A));
	}

    @Test
    public void testParser() {
        GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult(graph), result);
    }

    public static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(11)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}


    private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1);
        TerminalNode node1 = createTerminalNode(registry.getSlot("a"), 1, 2);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 2, 3);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node2);
        IntermediateNode node4 = createIntermediateNode(registry.getSlot("A ::= a A . b"), node1, node3);
        TerminalNode node5 = createTerminalNode(registry.getSlot("b"), 3, 4);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("A ::= a A b ."), node4, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a A b ."), node6);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("A ::= a A . b"), node0, node7);
        TerminalNode node9 = createTerminalNode(registry.getSlot("b"), 4, 5);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("A ::= a A b ."), node8, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a A b ."), node10);
        return  node11;
    }
}
