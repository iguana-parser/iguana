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
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;


import iguana.utils.input.Input;

/**
 * A ::= B C D
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'd'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test7 {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");
	static Terminal b = Terminal.from(Character.from('b'));
	static Terminal c = Terminal.from(Character.from('c'));
	static Terminal d = Terminal.from(Character.from('d'));

    static Rule r1 = Rule.withHead(A).addSymbols(B, C, D).build();
    static Rule r2 = Rule.withHead(B).addSymbol(b).build();
    static Rule r3 = Rule.withHead(C).addSymbol(c).build();
    static Rule r4 = Rule.withHead(D).addSymbol(d).build();
	
    private static Input input = Input.fromString("bcd");
    private static Nonterminal startSymbol = A;
	private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
    private static Terminal t2;
    private static Terminal t1;
    private static Terminal t0;

    @Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(B));
		assertFalse(firstFollowSets.isNullable(C));
		assertFalse(firstFollowSets.isNullable(D));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B, C, D), reachabilityGraph.getReachableNonterminals(A));
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
        assertEquals(getTree(), result.asParseSuccess().getTree());
    }

	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}

	private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(7)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(4)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(6)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		TerminalNode node0 = createTerminalNode(registry.getSlot("b"), 0, 1, input);
		NonterminalNode node1 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node0, input);
		TerminalNode node2 = createTerminalNode(registry.getSlot("c"), 1, 2, input);
		NonterminalNode node3 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node2, input);
		IntermediateNode node4 = createIntermediateNode(registry.getSlot("A ::= B C . D"), node1, node3);
		TerminalNode node5 = createTerminalNode(registry.getSlot("d"), 2, 3, input);
		NonterminalNode node6 = createNonterminalNode(registry.getSlot("D"), registry.getSlot("D ::= d ."), node5, input);
		IntermediateNode node7 = createIntermediateNode(registry.getSlot("A ::= B C D ."), node4, node6);
		NonterminalNode node8 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= B C D ."), node7, input);
		return node8;
	}

    public static Tree getTree() {
        Tree t0 = createTerminal(0, 1, input);
        Tree t1 = createTerminal(1, 2, input);
        Tree t2 = createTerminal(2, 3, input);
        Tree t3 = createRule(r2, list(t0), input);
        Tree t4 = createRule(r3, list(t1), input);
        Tree t5 = createRule(r4, list(t2), input);
        return createRule(r1, list(t3, t4, t5), input);
    }


}
