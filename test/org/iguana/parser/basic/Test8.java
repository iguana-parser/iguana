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
import iguana.parsetrees.tree.Tree;
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

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;

import iguana.utils.input.Input;

/**
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 */
public class Test8 {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c = Character.from('c');

	static Rule r1 = Rule.withHead(A).addSymbols(a, B, c).build();
	static Rule r2 = Rule.withHead(A).addSymbol(C).build();
	static Rule r3 = Rule.withHead(B).addSymbol(b).build();
	static Rule r4 = Rule.withHead(C).addSymbols(a, C).build();
	static Rule r5 = Rule.withHead(C).addSymbol(c).build();
    
    private static Input input1 = Input.fromString("abc");
    private static Input input2 = Input.fromString("aaaac");
    private static Nonterminal startSymbol = A;
    private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();


	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B, C), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(set(C), reachabilityGraph.getReachableNonterminals(C));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(B));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("A")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("B")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("C")));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}

	@Test
	public void testParser1_1() {
		GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.DEFAULT);
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input1, graph, startSymbol);
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1_Lookahead1(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser1_0() {
        GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.builder().setLookaheadCount(0).build());
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1_Lookahead0(graph), result);
        assertEquals(getTree1(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser2_1() {
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2_Lookahead1(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    @Test
    public void testParser2_0() {
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.builder().setLookaheadCount(0).build());
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2_Lookahead0(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }

    private static ParseSuccess getParseResult1_Lookahead0(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(8)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
	}
	
	private static ParseSuccess getParseResult1_Lookahead1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
	}
	
	private static ParseSuccess getParseResult2_Lookahead0(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(18)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
	}
	
	private static ParseSuccess getParseResult2_Lookahead1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(12)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
	}
	
	private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        TerminalNode node1 = createTerminalNode(registry.getSlot("b"), 1, 2, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node1, input1);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("A ::= a B . c"), node0, node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("c"), 2, 3, input1);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("A ::= a B c ."), node3, node4);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a B c ."), node5, input1);
        return node6;
	}

    public static Tree getTree1() {
        Tree t1 = createRule(r3, list(createTerminal(1, 2)));
        return createRule(r1, list(createTerminal(0, 1), t1, createTerminal(2, 3)));
    }

	private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        TerminalNode node1 = createTerminalNode(registry.getSlot("a"), 1, 2, input1);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 2, 3, input1);
        TerminalNode node3 = createTerminalNode(registry.getSlot("a"), 3, 4, input1);
        TerminalNode node4 = createTerminalNode(registry.getSlot("c"), 4, 5, input1);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node4, input1);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("C ::= a C ."), node3, node5);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= a C ."), node6, input1);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("C ::= a C ."), node2, node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= a C ."), node8, input1);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("C ::= a C ."), node1, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= a C ."), node10, input1);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("C ::= a C ."), node0, node11);
        NonterminalNode node13 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= a C ."), node12, input1);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= C ."), node13, input1);
        return node14;
    }

    public static Tree getTree2() {
        Tree t1 = createRule(r5, list(createTerminal(4, 5)));
        Tree t2 = createRule(r4, list(createTerminal(3, 4), t1));
        Tree t3 = createRule(r4, list(createTerminal(2, 3), t2));
        Tree t4 = createRule(r4, list(createTerminal(1, 2), t3));
        Tree t5 = createRule(r4, list(createTerminal(0, 1), t4));
        return createRule(r2, list(t5));
    }
	
}
	
