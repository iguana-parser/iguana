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

import com.google.common.collect.ImmutableSet;

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
		assertEquals(ImmutableSet.of(A, B, C, D), reachabilityGraph.getReachableNonterminals(S));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(B));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(C));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(D));
	}
	
	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(23)
				.setGSSNodesCount(8)
				.setGSSEdgesCount(7)
				.setNonterminalNodesCount(13)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(20)
				.setAmbiguousNodesCount(3).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 1);
		PackedNode node2 = factory.createPackedNode("S ::= A B C D .", 0, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A B C . D", 0, 0);
		PackedNode node4 = factory.createPackedNode("S ::= A B C . D", 0, node3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= A B . C D", 0, 0);
		PackedNode node6 = factory.createPackedNode("S ::= A B . C D", 0, node5);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node8 = factory.createPackedNode("A ::= .", 0, node7);
		TerminalNode node9 = factory.createEpsilonNode(0);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalNode node10 = factory.createNonterminalNode("B", 0, 0, 0);
		PackedNode node11 = factory.createPackedNode("B ::= .", 0, node10);
		node11.addChild(node9);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		node5.addChild(node6);
		NonterminalNode node13 = factory.createNonterminalNode("C", 0, 0, 0);
		PackedNode node14 = factory.createPackedNode("C ::= .", 0, node13);
		node14.addChild(node9);
		node13.addChild(node14);
		node4.addChild(node5);
		node4.addChild(node13);
		node3.addChild(node4);
		NonterminalNode node16 = factory.createNonterminalNode("D", 0, 0, 1);
		PackedNode node17 = factory.createPackedNode("D ::= a .", 1, node16);
		TerminalNode node18 = factory.createTerminalNode("a", 0, 1);
		node17.addChild(node18);
		node16.addChild(node17);
		node2.addChild(node3);
		node2.addChild(node16);
		PackedNode node19 = factory.createPackedNode("S ::= A B C D .", 1, node1);
		IntermediateNode node20 = factory.createIntermediateNode("S ::= A B C . D", 0, 1);
		PackedNode node21 = factory.createPackedNode("S ::= A B C . D", 0, node20);
		NonterminalNode node23 = factory.createNonterminalNode("C", 0, 0, 1);
		PackedNode node24 = factory.createPackedNode("C ::= a .", 1, node23);
		node24.addChild(node18);
		node23.addChild(node24);
		node21.addChild(node5);
		node21.addChild(node23);
		PackedNode node26 = factory.createPackedNode("S ::= A B C . D", 1, node20);
		IntermediateNode node27 = factory.createIntermediateNode("S ::= A B . C D", 0, 1);
		PackedNode node28 = factory.createPackedNode("S ::= A B . C D", 0, node27);
		NonterminalNode node30 = factory.createNonterminalNode("B", 0, 0, 1);
		PackedNode node31 = factory.createPackedNode("B ::= a .", 1, node30);
		node31.addChild(node18);
		node30.addChild(node31);
		node28.addChild(node7);
		node28.addChild(node30);
		PackedNode node33 = factory.createPackedNode("S ::= A B . C D", 1, node27);
		NonterminalNode node34 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node35 = factory.createPackedNode("A ::= a .", 1, node34);
		node35.addChild(node18);
		node34.addChild(node35);
		NonterminalNode node37 = factory.createNonterminalNode("B", 0, 1, 1);
		PackedNode node38 = factory.createPackedNode("B ::= .", 1, node37);
		TerminalNode node39 = factory.createEpsilonNode(1);
		node38.addChild(node39);
		node37.addChild(node38);
		node33.addChild(node34);
		node33.addChild(node37);
		node27.addChild(node28);
		node27.addChild(node33);
		NonterminalNode node40 = factory.createNonterminalNode("C", 0, 1, 1);
		PackedNode node41 = factory.createPackedNode("C ::= .", 1, node40);
		node41.addChild(node39);
		node40.addChild(node41);
		node26.addChild(node27);
		node26.addChild(node40);
		node20.addChild(node21);
		node20.addChild(node26);
		NonterminalNode node43 = factory.createNonterminalNode("D", 0, 1, 1);
		PackedNode node44 = factory.createPackedNode("D ::= .", 1, node43);
		node44.addChild(node39);
		node43.addChild(node44);
		node19.addChild(node20);
		node19.addChild(node43);
		node1.addChild(node2);
		node1.addChild(node19);
		return node1;
	}
}
