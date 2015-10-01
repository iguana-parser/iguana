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

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.SPPFToJavaCode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Tree;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.tree.TreeFactory.*;
import static org.iguana.util.CollectionsUtil.*;
import static org.junit.Assert.*;

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

    static Character a = Character.from('a');
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
        GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input, graph, startSymbol);
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
		return new ParseSuccess(expectedSPPF(graph), getTree(), statistics, input);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node0);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= ."), node0);
        IntermediateNode node3 = createIntermediateNode(registry.getSlot("S ::= A B . C D"), node1, node2);
        NonterminalNode node4 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= ."), node0);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("S ::= A B C . D"), node3, node4);
        TerminalNode node6 = createTerminalNode(registry.getSlot("a"), 0, 1);
        NonterminalNode node7 = createNonterminalNode(registry.getSlot("D"), registry.getSlot("D ::= a ."), node6);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= a ."), node6);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= a ."), node6);
        NonterminalNode node10 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node6);
        TerminalNode node11 = createTerminalNode(registry.getSlot("epsilon"), 1, 1);
        NonterminalNode node12 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= ."), node11);
        IntermediateNode node13 = createIntermediateNode(registry.getSlot("S ::= A B . C D"), node1, node9);
        node13.addPackedNode(registry.getSlot("S ::= A B . C D"), node10, node12);
        NonterminalNode node14 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= ."), node11);
        IntermediateNode node15 = createIntermediateNode(registry.getSlot("S ::= A B C . D"), node3, node8);
        node15.addPackedNode(registry.getSlot("S ::= A B C . D"), node13, node14);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("D"), registry.getSlot("D ::= ."), node11);
        IntermediateNode node17 = createIntermediateNode(registry.getSlot("S ::= A B C D ."), node5, node7);
        node17.addPackedNode(registry.getSlot("S ::= A B C D ."), node15, node16);
        NonterminalNode node18 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A B C D ."), node17);
        return node18;
    }

    private static Tree getTree() {
        Tree t0 = createEpsilon();
        Tree t1 = createRule(r3, list(t0));
        Tree t2 = createTerminal("a");
        Tree t3 = createRule(r4, list(t2));
        Tree t4 = createRule(r2, list(t2));
        Tree t5 = createRule(r5, list(t0));
        Tree t6 = createAmbiguity(set(createBranch(list(t4, t5)), createBranch(list(t1, t3))));
        Tree t7 = createRule(r7, list(t0));
        Tree t8 = createRule(r6, list(t2));
        Tree t9 = createAmbiguity(set(createBranch(list(t1, t5, t8)), createBranch(list(t6, t7))));
        Tree t10 = createRule(r9, list(t0));
        Tree t11 = createRule(r8, list(t2));
        Tree t12 = createAmbiguity(set(createBranch(list(t9, t10)), createBranch(list(t1, t5, t7, t11))));
        Tree t13 = createRule(r1, list(t12));
        return t13;
    }
}
