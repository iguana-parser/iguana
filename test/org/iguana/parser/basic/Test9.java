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
import iguana.parsetrees.tree.RuleNode;
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
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;

import iguana.utils.input.Input;

/**
 * S ::= 'a' A 'c'
 *     | 'a' A 'b'
 *     
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test9 {
	
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Terminal a = Terminal.from(Character.from('a'));
	static Terminal b = Terminal.from(Character.from('b'));
	static Terminal c = Terminal.from(Character.from('c'));

    static Rule r1 = Rule.withHead(S).addSymbols(a, A, c).build();
    static Rule r2 = Rule.withHead(S).addSymbols(a, A, b).build();
    static Rule r3 = Rule.withHead(A).addSymbol(a).build();
	
    private static Input input = Input.fromString("aab");

    private static Nonterminal startSymbol = S;

	private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();

	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(S));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(S));
		assertFalse(firstFollowSets.isNullable(A));
	}

	@Test
	public void testParser0() {
		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.builder().setLookaheadCount(0).build());
		ParseResult result = Iguana.parse(input, graph, startSymbol);
		assertTrue(result.isParseSuccess());
		assertEquals(getParseResult0(graph), result);
	}

	@Test
	public void testParser1() {
		GrammarGraph graph = GrammarGraph.from(grammar, input);
		ParseResult result = Iguana.parse(input, graph, startSymbol);
		assertTrue(result.isParseSuccess());
		assertEquals(getParseResult1(graph), result);
        assertEquals(getTree(), result.asParseSuccess().getTree());
	}

	private static ParseSuccess getParseResult0(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(5)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}

	private static ParseSuccess getParseResult1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(4)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input);
		TerminalNode node1 = createTerminalNode(registry.getSlot("a"), 1, 2, input);
		NonterminalNode node2 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node1, input);
		IntermediateNode node3 = createIntermediateNode(registry.getSlot("S ::= a A . b"), node0, node2);
		TerminalNode node4 = createTerminalNode(registry.getSlot("b"), 2, 3, input);
		IntermediateNode node5 = createIntermediateNode(registry.getSlot("S ::= a A b ."), node3, node4);
		NonterminalNode node6 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= a A b ."), node5, input);
		return node6;
	}

    public static RuleNode getTree() {
        Tree t1 = createRule(r3, list(createTerminal(1, 2, input)), input);
        return createRule(r2, list(createTerminal(0, 1, input), t1, createTerminal(2, 3, input)), input);
    }
}
	
