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
 * 
 * S ::= A B C D
 * A ::= 'a' | epsilon
 * B ::= 'a' | epsilon
 * C ::= 'a' | epsilon
 * D ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 */
@RunWith(Parameterized.class)
public class Test15 extends AbstractParserTest {

	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");
	
	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test15::getParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Nonterminal getStartSymbol() {
    	return S;
    }
    
    private static Input getInput() {
    	return Input.fromString("a");
    }
	
	private static Grammar getGrammar() {
		Character a = Character.from('a');
		Rule r1 = Rule.withHead(S).addSymbols(A, B, C, D).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).build();
		Rule r3 = Rule.withHead(A).build();
		Rule r4 = Rule.withHead(B).addSymbol(a).build();
		Rule r5 = Rule.withHead(B).build();
		Rule r6 = Rule.withHead(C).addSymbol(a).build();
		Rule r7 = Rule.withHead(C).build();
		Rule r8 = Rule.withHead(D).addSymbol(a).build();
		Rule r9 = Rule.withHead(D).build();

		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).
													   addRule(r4).addRule(r5).addRule(r6).
													   addRule(r7).addRule(r8).addRule(r9).build();
	}
	
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
	
	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(23)
				.setGSSNodesCount(8)
				.setGSSEdgesCount(7)
				.setNonterminalNodesCount(13)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(6)
				.setPackedNodesCount(22)
				.setAmbiguousNodesCount(3).build();
		return new ParseSuccess(expectedSPPF(registry), statistics, getInput());
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 1);
		PackedNode node2 = factory.createPackedNode("S ::= A B C D .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A B C D .", 0, 1);
		PackedNode node4 = factory.createPackedNode("S ::= A B C D .", 0, node3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= A B C . D", 0, 0);
		PackedNode node6 = factory.createPackedNode("S ::= A B C . D", 0, node5);
		IntermediateNode node7 = factory.createIntermediateNode("S ::= A B . C D", 0, 0);
		PackedNode node8 = factory.createPackedNode("S ::= A B . C D", 0, node7);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node10 = factory.createPackedNode("A ::= .", 0, node9);
		TerminalNode node11 = factory.createEpsilonNode(0);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalNode node12 = factory.createNonterminalNode("B", 0, 0, 0);
		PackedNode node13 = factory.createPackedNode("B ::= .", 0, node12);
		node13.addChild(node11);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node7.addChild(node8);
		NonterminalNode node15 = factory.createNonterminalNode("C", 0, 0, 0);
		PackedNode node16 = factory.createPackedNode("C ::= .", 0, node15);
		node16.addChild(node11);
		node15.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node15);
		node5.addChild(node6);
		NonterminalNode node18 = factory.createNonterminalNode("D", 0, 0, 1);
		PackedNode node19 = factory.createPackedNode("D ::= a .", 1, node18);
		TerminalNode node20 = factory.createTerminalNode("a", 0, 1);
		node19.addChild(node20);
		node18.addChild(node19);
		node4.addChild(node5);
		node4.addChild(node18);
		PackedNode node21 = factory.createPackedNode("S ::= A B C D .", 1, node3);
		IntermediateNode node22 = factory.createIntermediateNode("S ::= A B C . D", 0, 1);
		PackedNode node23 = factory.createPackedNode("S ::= A B C . D", 0, node22);
		NonterminalNode node25 = factory.createNonterminalNode("C", 0, 0, 1);
		PackedNode node26 = factory.createPackedNode("C ::= a .", 1, node25);
		node26.addChild(node20);
		node25.addChild(node26);
		node23.addChild(node7);
		node23.addChild(node25);
		PackedNode node28 = factory.createPackedNode("S ::= A B C . D", 1, node22);
		IntermediateNode node29 = factory.createIntermediateNode("S ::= A B . C D", 0, 1);
		PackedNode node30 = factory.createPackedNode("S ::= A B . C D", 0, node29);
		NonterminalNode node32 = factory.createNonterminalNode("B", 0, 0, 1);
		PackedNode node33 = factory.createPackedNode("B ::= a .", 1, node32);
		node33.addChild(node20);
		node32.addChild(node33);
		node30.addChild(node9);
		node30.addChild(node32);
		PackedNode node35 = factory.createPackedNode("S ::= A B . C D", 1, node29);
		NonterminalNode node36 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node37 = factory.createPackedNode("A ::= a .", 1, node36);
		node37.addChild(node20);
		node36.addChild(node37);
		NonterminalNode node39 = factory.createNonterminalNode("B", 0, 1, 1);
		PackedNode node40 = factory.createPackedNode("B ::= .", 1, node39);
		TerminalNode node41 = factory.createEpsilonNode(1);
		node40.addChild(node41);
		node39.addChild(node40);
		node35.addChild(node36);
		node35.addChild(node39);
		node29.addChild(node35);
		node29.addChild(node35);
		NonterminalNode node42 = factory.createNonterminalNode("C", 0, 1, 1);
		PackedNode node43 = factory.createPackedNode("C ::= .", 1, node42);
		node43.addChild(node41);
		node42.addChild(node43);
		node28.addChild(node29);
		node28.addChild(node42);
		node22.addChild(node28);
		node22.addChild(node28);
		NonterminalNode node45 = factory.createNonterminalNode("D", 0, 1, 1);
		PackedNode node46 = factory.createPackedNode("D ::= .", 1, node45);
		node46.addChild(node41);
		node45.addChild(node46);
		node21.addChild(node22);
		node21.addChild(node45);
		node3.addChild(node21);
		node3.addChild(node21);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
}
