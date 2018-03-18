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
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.ParseStatistics;
import org.iguana.util.TestRunner;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.*;

/**
 * A ::= B C
 * B ::= 'b'
 * C ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test6 {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Terminal b = Terminal.from(Char.from('b'));
	static Terminal c = Terminal.from(Char.from('c'));

    static Rule r1 = Rule.withHead(A).addSymbols(B, C).build();
    static Rule r2 = Rule.withHead(B).addSymbol(b).build();
    static Rule r3 = Rule.withHead(C).addSymbol(c).build();

    private static Start startSymbol = Start.from(A);
    private static Grammar grammar = new DesugarStartSymbol().transform(Grammar.builder().addRule(r1).addRule(r2).addRule(r3).setStartSymbol(startSymbol).build());

    private static Input input = Input.fromString("bc");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        TestRunner.record(grammar, input, 1, path + "/Test6");
    }

    @Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("A")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("B")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("C")));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B, C), reachabilityGraph.getReachableNonterminals(A));
	}

	@Test
	public void testParser() {
		ParseResult result = Iguana.parse(input, grammar, startSymbol);
		assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar, input);
        assertEquals(getParseResult(graph), result);
    }
	
	private static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(2)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}

    private static NonterminalNode expectedSPPF(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("b", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("B", "B ::= b .", node0);
        TerminalNode node2 = factory.createTerminalNode("c", 1, 2);
        NonterminalNode node3 = factory.createNonterminalNode("C", "C ::= c .", node2);
        IntermediateNode node4 = factory.createIntermediateNode("A ::= B C .", node1, node3);
        NonterminalNode node5 = factory.createNonterminalNode("A", "A ::= B C .", node4);
        return node5;
    }

}
