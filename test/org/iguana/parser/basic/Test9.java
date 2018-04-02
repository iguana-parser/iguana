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
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.*;

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
	static Terminal a = Terminal.from(Char.from('a'));
	static Terminal b = Terminal.from(Char.from('b'));
	static Terminal c = Terminal.from(Char.from('c'));

    static Rule r1 = Rule.withHead(S).addSymbols(a, A, c).build();
    static Rule r2 = Rule.withHead(S).addSymbols(a, A, b).build();
    static Rule r3 = Rule.withHead(A).addSymbol(a).build();

    private static Start startSymbol = Start.from(S);
    private static Grammar grammar = new DesugarStartSymbol().transform(Grammar.builder().addRule(r1).addRule(r2).addRule(r3).setStartSymbol(startSymbol).build());

    private static Input input = Input.fromString("aab");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        TestRunner.record(grammar, input, 1, path + "/Test9");
    }

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
		ParseResult result = Iguana.parse(input, grammar);
        assertTrue(result.isParseSuccess());
		GrammarGraph graph = GrammarGraph.from(grammar, Configuration.builder().setLookaheadCount(0).build());
		assertEquals(getParseResult0(graph), result);
	}

	@Test
	public void testParser1() {
        ParseResult result = Iguana.parse(input, grammar);
		assertTrue(result.isParseSuccess());
		GrammarGraph graph = GrammarGraph.from(grammar);
		assertEquals(getParseResult1(graph), result);
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
		return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
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
		return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
	}
	
	private static org.iguana.sppf.NonterminalNode expectedSPPF(SPPFNodeFactory factory) {
		TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
		TerminalNode node1 = factory.createTerminalNode("a", 1, 2);
		org.iguana.sppf.NonterminalNode node2 = factory.createNonterminalNode("A", "A ::= a .", node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= a A . b", node0, node2);
		TerminalNode node4 = factory.createTerminalNode("b", 2, 3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= a A b .", node3, node4);
		org.iguana.sppf.NonterminalNode node6 = factory.createNonterminalNode("S", "S ::= a A b .", node5);
		return node6;
	}

}
	
