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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * S ::= A B C
 *     | A B D
 *     
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test10 {
    
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");
	static Terminal a = Terminal.from(Char.from('a'));
	static Terminal b = Terminal.from(Char.from('b'));
	static Terminal c = Terminal.from(Char.from('c'));

	static Rule r1 = Rule.withHead(S).addSymbols(A, B, C).build();
    static Rule r2 = Rule.withHead(S).addSymbols(A, B, D).build();
    static Rule r3 = Rule.withHead(A).addSymbol(a).build();
    static Rule r4 = Rule.withHead(B).addSymbol(b).build();
    static Rule r5 = Rule.withHead(C).addSymbol(c).build();
    static Rule r6 = Rule.withHead(D).addSymbol(c).build();

    private static Start startSymbol = Start.from(S);
    private static Grammar grammar = new DesugarStartSymbol().transform(Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).setStartSymbol(startSymbol).build());

    private static Input input =  Input.fromString("abc");

    @BeforeClass
    public static void record() {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        TestRunner.record(grammar, input, 1, path + "/Test10");
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
        ParseResult result = Iguana.parse(input, grammar);
		assertTrue(result.isParseSuccess());
        GrammarGraph graph = GrammarGraph.from(grammar, input, new ParserResultOps());
        assertEquals(getParseResult(graph), result);
    }
	
	private static ParseResult getParseResult(GrammarGraph graph) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(12)
                .setGSSNodesCount(5)
                .setGSSEdgesCount(6)
                .setNonterminalNodesCount(5)
                .setTerminalNodesCount(3)
                .setIntermediateNodesCount(4)
                .setPackedNodesCount(10)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(expectedSPPF(new SPPFNodeFactory(graph)), statistics, input);
    }

	private static NonterminalNode expectedSPPF(SPPFNodeFactory factory) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1);
        NonterminalNode node1 = factory.createNonterminalNode("A", "A ::= a .", node0);
        TerminalNode node2 = factory.createTerminalNode("b", 1, 2);
        NonterminalNode node3 = factory.createNonterminalNode("B", "B ::= b .", node2);
        IntermediateNode node4 = factory.createIntermediateNode("S ::= A B . D", node1, node3);
        TerminalNode node5 = factory.createTerminalNode("c", 2, 3);
        NonterminalNode node6 = factory.createNonterminalNode("D", "D ::= c .", node5);
        IntermediateNode node7 = factory.createIntermediateNode("S ::= A B D .", node4, node6);
        IntermediateNode node8 = factory.createIntermediateNode("S ::= A B . C", node1, node3);
        NonterminalNode node9 = factory.createNonterminalNode("C", "C ::= c .", node5);
        IntermediateNode node10 = factory.createIntermediateNode("S ::= A B C .", node8, node9);
        NonterminalNode node11 = factory.createNonterminalNode("S", "S ::= A B D .", node7);
        node11.addPackedNode(factory.createPackedNode("S ::= A B C .", node10));
        return node11;
    }

}
	
