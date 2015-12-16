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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.term.Term;
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

import iguana.utils.input.Input;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.assertTrue;

/**
 * 
 * A ::=  B 'a'
 * B ::= 'b'
 * 
 * @author Ali Afroozeh
 */
public class Test15 {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Terminal a = Terminal.from(Character.from('a'));
	static Terminal b = Terminal.from(Character.from('b'));
	static Rule r1 = Rule.withHead(A).addSymbols(B, a).build();
	static Rule r2 = Rule.withHead(B).addSymbol(b).build();

	private static Nonterminal startSymbol = A;
	private static Input input = Input.fromString("ba");
	private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).build();;


	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(B));
	}

	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(B));
	}

    @Test
    public void testParser() {
        GrammarGraph graph = GrammarGraph.from(grammar, input);
        ParseResult result = Iguana.parse(input, graph, startSymbol);
        assertEquals(getParseResult(graph), result);
        assertTrue(getTree().equals(result.asParseSuccess().getTree()));
    }
	
	private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(3)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(1)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(2)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(graph), statistics, input);
	}
		
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("b"), 0, 1, input);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node0, input);
        TerminalNode node2 = createTerminalNode(registry.getSlot("a"), 1, 2, input);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("A ::= B a ."), node1, node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= B a ."), node3, input);
        return node4;
 	}

    private static Term getTree() {
        Term t0 = createTerminal(b, 0, 1, input);
        Term t1 = createRule(r2, list(t0), input);
        Term t2 = createTerminal(a, 1, 2, input);
        Term t3 = createRule(r1, list(t1, t2), input);
        return t3;
    }
}
