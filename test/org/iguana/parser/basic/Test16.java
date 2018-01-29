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
 * S ::= A B C D
 * A ::= 'a' | epsilon
 * B ::= 'a' | epsilon
 * C ::= 'a' | epsilon
 * D ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 */
public class Test16 {

	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");

    static Terminal a = Terminal.from(Char.from('a'));
    static Rule r1 = Rule.withHead(S).addSymbols(A, B, C, D).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).build();
    static Rule r3 = Rule.withHead(A).build();
    static Rule r4 = Rule.withHead(B).addSymbol(a).build();
    static Rule r5 = Rule.withHead(B).build();
    static Rule r6 = Rule.withHead(C).addSymbol(a).build();
    static Rule r7 = Rule.withHead(C).build();
    static Rule r8 = Rule.withHead(D).addSymbol(a).build();
    static Rule r9 = Rule.withHead(D).build();
	
    private static Nonterminal startSymbol = S;
    private static Input input = Input.fromString("a");
	private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).
                                                       addRule(r4).addRule(r5).addRule(r6).
                                                       addRule(r7).addRule(r8).addRule(r9).build();


	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertTrue(firstFollowSets.isNullable(S));
		assertTrue(firstFollowSets.isNullable(A));
		assertTrue(firstFollowSets.isNullable(B));
		assertTrue(firstFollowSets.isNullable(C));
		assertTrue(firstFollowSets.isNullable(D));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A, B, C, D), reachabilityGraph.getReachableNonterminals(S));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(B));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(C));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(D));
	}

    @Test
    public void testParser() {
        GrammarGraph graph = GrammarGraph.from(grammar, input);
        ParseResult result = Iguana.parse(input, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult(graph), result);
    }

    private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(18)
				.setGSSNodesCount(8)
				.setGSSEdgesCount(7)
				.setNonterminalNodesCount(11)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(5)
				.setPackedNodesCount(19)
				.setAmbiguousNodesCount(3).build();
		return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("epsilon", 0, 0, input);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= .", node0, input);
        NonterminalNode node2 = factory.createNonterminalNode("B", "B ::= .", node0, input);
        IntermediateNode node3 = factory.createIntermediateNode("S ::= A B . C D", node1, node2);
        NonterminalNode node4 = factory.createNonterminalNode("C", "C ::= .", node0, input);
        IntermediateNode node5 = factory.createIntermediateNode("S ::= A B C . D", node3, node4);
        TerminalNode node6 = factory.createTerminalNode("a", 0, 1, input);
        NonterminalNode node7 = factory.createNonterminalNode("D", "D ::= a .", node6, input);
        NonterminalNode node8 = factory.createNonterminalNode("C", "C ::= a .", node6, input);
        NonterminalNode node9 = factory.createNonterminalNode("B", "B ::= a .", node6, input);
        NonterminalNode node10 = factory.createNonterminalNode("A", "A ::= a .", node6, input);
        TerminalNode node11 = factory.createTerminalNode("epsilon", 1, 1, input);
        NonterminalNode node12 = factory.createNonterminalNode("B", "B ::= .", node11, input);
        IntermediateNode node13 = factory.createIntermediateNode("S ::= A B . C D", node1, node9);
        node13.addPackedNode(factory.createPackedNode("S ::= A B . C D", node10, node12));
        NonterminalNode node14 = factory.createNonterminalNode("C", "C ::= .", node11, input);
        IntermediateNode node15 = factory.createIntermediateNode("S ::= A B C . D", node3, node8);
        node15.addPackedNode(factory.createPackedNode("S ::= A B C . D", node13, node14));
        NonterminalNode node16 = factory.createNonterminalNode("D", "D ::= .", node11, input);
        IntermediateNode node17 = factory.createIntermediateNode("S ::= A B C D .", node5, node7);
        node17.addPackedNode(factory.createPackedNode("S ::= A B C D .", node15, node16));
        NonterminalNode node18 = factory.createNonterminalNode("S", "S ::= A B C D .", node17, input);
        return node18;
    }

}
