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
 * 
 * A ::= a A b
 *	   | a A c
 *     | a
 *     
 * @author Ali Afroozeh
 * 
 */
public class Test17 {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Terminal a = Terminal.from(Char.from('a'));
	static Terminal b = Terminal.from(Char.from('b'));
	static Terminal c = Terminal.from(Char.from('c'));

    static Rule r1 = Rule.withHead(A).addSymbols(a, A, b).build();
    static Rule r2 = Rule.withHead(A).addSymbols(a, A, c).build();
    static Rule r3 = Rule.withHead(A).addSymbols(a).build();

    static Start startSymbol = Start.from(A);
    static Grammar grammar = new DesugarStartSymbol().transform(Grammar.builder().addRules(r1, r2, r3).setStartSymbol(startSymbol).build());

    static Input input = Input.fromString("aaabb");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        TestRunner.record(grammar, input, 1, path + "/Test17");
    }

    @Test
	public void testNullable() {
        FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(A));
	}

    @Test
    public void testParser() {
        ParseResult result = Iguana.parse(input, grammar);
        assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar);
        assertEquals(getParseResult(graph), result);
    }

    public static ParseSuccess getParseResult(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(11)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
	}


    private static NonterminalNode expectedSPPF(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        TerminalNode node1 = factory.createTerminalNode("a", 1, 2);
        TerminalNode node2 = factory.createTerminalNode("a", 2, 3);
        NonterminalNode node3 = factory.createNonterminalNode("A", "A ::= a .", node2);
        IntermediateNode node4 = factory.createIntermediateNode("A ::= a A . b", node1, node3);
        TerminalNode node5 = factory.createTerminalNode("b", 3, 4);
        IntermediateNode node6 = factory.createIntermediateNode("A ::= a A b .", node4, node5);
        NonterminalNode node7 = factory.createNonterminalNode("A", "A ::= a A b .", node6);
        IntermediateNode node8 = factory.createIntermediateNode("A ::= a A . b", node0, node7);
        TerminalNode node9 = factory.createTerminalNode("b", 4, 5);
        IntermediateNode node10 = factory.createIntermediateNode("A ::= a A b .", node8, node9);
        NonterminalNode node11 = factory.createNonterminalNode("A", "A ::= a A b .", node10);
        return  node11;
    }

}
