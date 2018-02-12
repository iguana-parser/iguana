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

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.*;

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
	static Terminal a = Terminal.from(Char.from('a'));
	static Terminal b = Terminal.from(Char.from('b'));
	static Terminal c = Terminal.from(Char.from('c'));

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
		GrammarGraph graph = GrammarGraph.from(grammar, input1);
		ParseResult result = Iguana.parse(input1, graph, startSymbol);
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1_Lookahead1(graph), result);
    }

    @Test
    public void testParser1_0() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.builder().setLookaheadCount(0).build());
        ParseResult result = Iguana.parse(input1, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1_Lookahead0(graph), result);
    }

    @Test
    public void testParser2_1() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2_Lookahead1(graph), result);
    }

    @Test
    public void testParser2_0() {
        GrammarGraph graph = GrammarGraph.from(grammar, input1, Configuration.builder().setLookaheadCount(0).build());
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2_Lookahead0(graph), result);
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
		return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
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
		return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
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
		return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input2);
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
		return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input2);
	}
	
	private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        TerminalNode node1 = factory.createTerminalNode("b", 1, 2);
        NonterminalNode node2 = factory.createNonterminalNode("B", "B ::= b .", node1);
        IntermediateNode node3 = factory.createIntermediateNode("A ::= a B . c", node0, node2);
        TerminalNode node4 = factory.createTerminalNode("c", 2, 3);
        IntermediateNode node5 = factory.createIntermediateNode("A ::= a B c .", node3, node4);
        NonterminalNode node6 = factory.createNonterminalNode("A", "A ::= a B c .", node5);
        return node6;
	}

	private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        TerminalNode node1 = factory.createTerminalNode("a", 1, 2);
        TerminalNode node2 = factory.createTerminalNode("a", 2, 3);
        TerminalNode node3 = factory.createTerminalNode("a", 3, 4);
        TerminalNode node4 = factory.createTerminalNode("c", 4, 5);
        NonterminalNode node5 = factory.createNonterminalNode("C", "C ::= c .", node4);
        IntermediateNode node6 = factory.createIntermediateNode("C ::= a C .", node3, node5);
        NonterminalNode node7 = factory.createNonterminalNode("C", "C ::= a C .", node6);
        IntermediateNode node8 = factory.createIntermediateNode("C ::= a C .", node2, node7);
        NonterminalNode node9 = factory.createNonterminalNode("C", "C ::= a C .", node8);
        IntermediateNode node10 = factory.createIntermediateNode("C ::= a C .", node1, node9);
        NonterminalNode node11 = factory.createNonterminalNode("C", "C ::= a C .", node10);
        IntermediateNode node12 = factory.createIntermediateNode("C ::= a C .", node0, node11);
        NonterminalNode node13 = factory.createNonterminalNode("C", "C ::= a C .", node12);
        NonterminalNode node14 = factory.createNonterminalNode("A", "A ::= C .", node13);
        return node14;
    }
	
}
	
