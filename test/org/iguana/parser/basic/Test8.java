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

import static org.iguana.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.iguana.AbstractParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.ParseStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.iguana.util.CollectionsUtil.*;

/**
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test8 extends AbstractParserTest {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c = Character.from('c');

	@Parameters
    public static Collection<Object[]> data() {
		List<Object[]> parameters = 
			all_configs.stream().map(c -> new Object[] {
	    		getInput1(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test8::getParseResult1
	    	}).collect(Collectors.toList());
		parameters.addAll(all_configs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test8::getParseResult2
	    	}).collect(Collectors.toList()));
		return parameters;
    }
    
    private static Input getInput1() {
    	return Input.fromString("abc");
    }
    
    private static Input getInput2() {
    	return Input.fromString("aaaac");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
    
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B, C), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(set(C), reachabilityGraph.getReachableNonterminals(C));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(B));
	}
	
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(a, B, c).build();
		Rule r2 = Rule.withHead(A).addSymbol(C).build();
		Rule r3 = Rule.withHead(B).addSymbol(b).build();
		Rule r4 = Rule.withHead(C).addSymbols(a, C).build();
		Rule r5 = Rule.withHead(C).addSymbol(c).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("A")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("B")));
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("C")));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	private static ParseSuccess getParseResult1(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(8)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult2(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(18)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(6)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF2(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3);
		PackedNode node2 = factory.createPackedNode("A ::= a B c .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= a B . c", 0, 2);
		PackedNode node4 = factory.createPackedNode("A ::= a B . c", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("B", 1, 2);
		PackedNode node7 = factory.createPackedNode("B ::= b .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("b", 1, 2);
		node7.addChild(node8);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node9 = factory.createTerminalNode("c", 2, 3);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 5);
		PackedNode node2 = factory.createPackedNode("A ::= C .", 5, node1);
		NonterminalNode node3 = factory.createNonterminalNode("C", 0, 5);
		PackedNode node4 = factory.createPackedNode("C ::= a C .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("C", 1, 5);
		PackedNode node7 = factory.createPackedNode("C ::= a C .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
		NonterminalNode node9 = factory.createNonterminalNode("C", 2, 5);
		PackedNode node10 = factory.createPackedNode("C ::= a C .", 3, node9);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 3);
		NonterminalNode node12 = factory.createNonterminalNode("C", 3, 5);
		PackedNode node13 = factory.createPackedNode("C ::= a C .", 4, node12);
		TerminalNode node14 = factory.createTerminalNode("a", 3, 4);
		NonterminalNode node15 = factory.createNonterminalNode("C", 4, 5);
		PackedNode node16 = factory.createPackedNode("C ::= c .", 5, node15);
		TerminalNode node17 = factory.createTerminalNode("c", 4, 5);
		node16.addChild(node17);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		node12.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node12);
		node9.addChild(node10);
		node7.addChild(node8);
		node7.addChild(node9);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
	
}
	
