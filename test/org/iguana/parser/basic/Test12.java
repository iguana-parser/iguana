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
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * 
 * A ::= A A | a | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test12 {
	
	static Nonterminal A = Nonterminal.withName("A");
    static Terminal a = Terminal.from(Char.from('a'));
    static Rule r1 = Rule.withHead(A).addSymbols(A, A).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).build();
    static Rule r3 = Rule.withHead(A).build();
    static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
    static Nonterminal startSymbol = A;

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.empty();

	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(A));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertTrue(firstFollowSets.isNullable(A));
	}

	@Test
	public void testParser1() {
		GrammarGraph graph = GrammarGraph.from(grammar, input1);
		ParseResult result = Iguana.parse(input1, graph, startSymbol);
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
	}
	
	private static ParseSuccess getParseResult1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(11)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(5)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(4).build();
		return new ParseSuccess(expectedSPPF1(new SPPFNodeFactory(graph)), statistics, input1);
	}

    @Test
    public void testParser2() {
        GrammarGraph graph = GrammarGraph.from(grammar, input2);
        ParseResult result = Iguana.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
    }
	
	private static ParseSuccess getParseResult2(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF2(new SPPFNodeFactory(graph)), statistics, input2);
	}

    static NonterminalNode A_0_1;
    static NonterminalNode A_0_0;
    static NonterminalNode A_1_1;

	private static NonterminalNode expectedSPPF1(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        TerminalNode node1 = factory.createTerminalNode("epsilon", 1, 1);
        NonterminalNode node3 = factory.createNonterminalNode("A", "A ::= .", node1);
        IntermediateNode node2 = factory.createIntermediateNode("A ::= A A .", node3, node3);
        node3.addPackedNode(factory.createPackedNode("A ::= A A .", node2));
        TerminalNode node4 = factory.createTerminalNode("epsilon", 0, 0);
        NonterminalNode node6 = factory.createNonterminalNode("A", "A ::= .", node4);
        IntermediateNode node5 = factory.createIntermediateNode("A ::= A A .", node6, node6);
        node6.addPackedNode(factory.createPackedNode("A ::= A A .", node5));
        NonterminalNode node8 = factory.createNonterminalNode("A", "A ::= a .", node0);
        IntermediateNode node7 = factory.createIntermediateNode("A ::= A A .", node8, node3);
        node7.addPackedNode(factory.createPackedNode("A ::= A A .", node6, node8));
        node8.addPackedNode(factory.createPackedNode("A ::= A A .", node7));
        A_0_0 = node6;
        A_1_1 = node3;
        A_0_1 = node8;
        return node8;
	}

 	private static NonterminalNode expectedSPPF2(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0);
        NonterminalNode node2 = factory.createNonterminalNode("A", "A ::= .", node0);
        A_0_0 = node2;
        IntermediateNode node1 = factory.createIntermediateNode("A ::= A A .", node2, node2);
        node2.addPackedNode(factory.createPackedNode("A ::= A A .", node1));
        return node2;
	}
}
