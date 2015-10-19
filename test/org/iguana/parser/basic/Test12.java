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
import static org.iguana.util.CollectionsUtil.list;
import static org.iguana.util.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * 
 * A ::= A A | a | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test12 {
	
	static Nonterminal A = Nonterminal.withName("A");
    static Rule r1 = Rule.withHead(A).addSymbols(A, A).build();
    static Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
    static Rule r3 = Rule.withHead(A).build();
    static Grammar grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
    static Nonterminal startSymbol = A;

    private static Input input1 = Input.fromString("a");
    private static Input input2 = Input.empty();

	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(A), reachabilityGraph.getReachableNonterminals(A));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertTrue(firstFollowSets.isNullable(A));
	}

	@Test
	public void testParser1() {
		GrammarGraph graph = grammar.toGrammarGraph(input1, Configuration.DEFAULT);
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input1, graph, startSymbol);
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult1(graph), result);
//        assertEquals(getTree1(), result.asParseSuccess().getTree());
	}
	
	private static ParseSuccess getParseResult1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(11)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(5)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(4).build();
		return new ParseSuccess(expectedSPPF1(graph), statistics, input1);
	}

    @Test
    public void testParser2() {
        GrammarGraph graph = grammar.toGrammarGraph(input2, Configuration.DEFAULT);
        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input2, graph, startSymbol);
        assertTrue(result.isParseSuccess());
        assertEquals(getParseResult2(graph), result);
        assertEquals(getTree2(), result.asParseSuccess().getTree());
    }
	
	private static ParseSuccess getParseResult2(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF2(graph), statistics, input2);
	}
		
	private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input1);
        TerminalNode node1 = createTerminalNode(registry.getSlot("epsilon"), 1, 1, input1);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node1, input1);
        IntermediateNode node2 = createIntermediateNode(registry.getSlot("A ::= A A ."), node3, node3);
        node3.addPackedNode(registry.getSlot("A ::= A A ."), node2);
        TerminalNode node4 = createTerminalNode(registry.getSlot("epsilon"), 0, 0, input1);
        NonterminalNode node6 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node4, input1);
        IntermediateNode node5 = createIntermediateNode(registry.getSlot("A ::= A A ."), node6, node6);
        node6.addPackedNode(registry.getSlot("A ::= A A ."), node5);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input1);
        IntermediateNode node7 = createIntermediateNode(registry.getSlot("A ::= A A ."), node8, node3);
        node7.addPackedNode(registry.getSlot("A ::= A A ."), node6, node8);
        node8.addPackedNode(registry.getSlot("A ::= A A ."), node7);
        return node8;
	}

 	private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("epsilon"), 0, 0, input1);
        NonterminalNode node2 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= ."), node0, input1);
        IntermediateNode node1 = createIntermediateNode(registry.getSlot("A ::= A A ."), node2, node2);
        node2.addPackedNode(registry.getSlot("A ::= A A ."), node1);
        return node2;
	}

    private static Tree getTree1() {
        Tree t0 = createTerminal(0, 1, input1);
        Tree t1 = createCycle("A");
        Tree t2 = createEpsilon(1);
        Tree t3 = createAmbiguity(set(createBranch(list(t2)), createBranch(list(t1, t1))));
        Tree t4 = createEpsilon(0);
        Tree t5 = createAmbiguity(set(createBranch(list(t4)), createBranch(list(t1, t1))));
        Tree t6 = createAmbiguity(set(createBranch(list(t5, t1)), createBranch(list(t1, t3))));
        Tree t7 = createAmbiguity(set(createBranch(list(t0)), createBranch(list(t6))));
        return t7;
    }

    private static Tree getTree2() {
        Tree t0 = createCycle("A");
        Tree t1 = createEpsilon(0);
        Tree t2 = createAmbiguity(set(createBranch(list(t0, t0)), createBranch(list(t1))));
        return t2;
    }

}
