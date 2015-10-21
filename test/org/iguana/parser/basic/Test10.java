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
import static org.junit.Assert.assertTrue;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.parsetrees.tree.Branch;
import iguana.parsetrees.tree.Tree;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
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
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c = Character.from('c');

	static Rule r1 = Rule.withHead(S).addSymbols(A, B, C).build();
    static Rule r2 = Rule.withHead(S).addSymbols(A, B, D).build();
    static Rule r3 = Rule.withHead(A).addSymbol(a).build();
    static Rule r4 = Rule.withHead(B).addSymbol(b).build();
    static Rule r5 = Rule.withHead(C).addSymbol(c).build();
    static Rule r6 = Rule.withHead(D).addSymbol(c).build();

	private static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).build();
	private static Input input =  Input.fromString("abc");
	private static Nonterminal startSymbol = S;

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
		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
		ParseResult result = Iguana.parse(input, graph, startSymbol);
		assertTrue(result.isParseSuccess());
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
        return new ParseSuccess(expectedSPPF(graph), statistics, input);
    }

	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input);
        TerminalNode node2 = createTerminalNode(registry.getSlot("b"), 1, 2, input);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= b ."), node2, input);
        IntermediateNode node4 = createIntermediateNode(registry.getSlot("S ::= A B . D"), node1, node3);
        TerminalNode node5 = createTerminalNode(registry.getSlot("c"), 2, 3, input);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("D"), registry.getSlot("D ::= c ."), node5, input);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("S ::= A B D ."), node4, node6);
        IntermediateNode node8 = createIntermediateNode(registry.getSlot("S ::= A B . C"), node1, node3);
        NonterminalNode node9 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node5, input);
        IntermediateNode node10 = createIntermediateNode(registry.getSlot("S ::= A B C ."), node8, node9);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A B D ."), node7, input);
        node11.addPackedNode(registry.getSlot("S ::= A B C ."), node10);
        return node11;
    }

    public static Tree getTree() {
        Tree t1 = createRule(r3, list(createTerminal(0, 1, input)), input); // A(a)
        Tree t2 = createRule(r4, list(createTerminal(1, 2, input)), input); // B(b)
        Tree t3 = createRule(r5, list(createTerminal(2, 3, input)), input); // C(c)
        Tree t4 = createRule(r6, list(createTerminal(2, 3, input)), input); // D(c)
        Branch<Tree> b1 = createBranch(list(t1, t2, t3));
        Branch<Tree> b2 = createBranch(list(t1, t2, t4));
        return createAmbiguity(set(b1, b2));
    }
}
	
