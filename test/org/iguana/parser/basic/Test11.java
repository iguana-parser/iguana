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

import static org.iguana.util.CollectionsUtil.list;
import static org.iguana.util.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import iguana.parsetrees.sppf.*;
import iguana.parsetrees.tree.Tree;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import iguana.utils.input.Input;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;

/**
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test11 {
	
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
    static Rule r1 = Rule.withHead(S).addSymbols(A, A, b).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).build();
    static Rule r3 = Rule.withHead(A).build();
    static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();;

    private static Input input = Input.fromString("ab");
    private static Nonterminal startSymbol = S;


    @Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(S));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(A));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertTrue(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(S));
	}

    @Test
    public void testParser() {
        GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, startSymbol);
        assertEquals(getParseResult(graph), result);
        assertEquals(getTree(), result.asParseSuccess().getTree());
    }

	private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(7)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(4)
				.setTerminalNodesCount(4)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0, input);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node0, input);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 0, 1, input);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node2, input);
        TerminalNode node4 = createTerminalNode(registry.getSlot("epsilon"), 1, 1, input);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node4, input);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("S ::= A A . b"), node1, node3);
        node6.addPackedNode(registry.getSlot("S ::= A A . b"), node3, node5);
        TerminalNode node7 = createTerminalNode(registry.getSlot("b"), 1, 2, input);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("S ::= A A b ."), node6, node7);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A A b ."), node8, input);
        return node9;
	}

    public static Tree getTree() {
        Tree t0 = createEpsilon(0);
        Tree t1 = createRule(r3, list(t0), input);
        Tree t2 = createTerminal(0, 1, input);
        Tree t3 = createRule(r2, list(t2), input);
        Tree t4 = createEpsilon(1);
        Tree t5 = createRule(r3, list(t4), input);
        Tree t6 = createAmbiguity(set(createBranch(list(t1, t3)), createBranch(list(t3, t5))));
        Tree t7 = createTerminal(1, 2, input);
        Tree t8 = createRule(r1, list(t6, t7), input);
        return t8;
    }
}
	
