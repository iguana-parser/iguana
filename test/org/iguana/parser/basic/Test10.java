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

import static org.iguana.util.CollectionsUtil.set;
import static org.iguana.util.Configurations.all_configs;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.iguana.AbstractParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
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
@RunWith(Parameterized.class)
public class Test10 extends AbstractParserTest {
    
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c = Character.from('c');
	
	@Parameters
    public static Collection<Object[]> data() {
		List<Object[]> parameters = all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test10::getNewParseResult
	    	}).collect(Collectors.toList());
		return parameters;
    }
	
    private static Input getInput() {
    	return Input.fromString("abc");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("S");
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
    
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(S).addSymbols(A, B, C).build();
		Rule r2 = Rule.withHead(S).addSymbols(A, B, D).build();
		Rule r3 = Rule.withHead(A).addSymbol(a).build();
		Rule r4 = Rule.withHead(B).addSymbol(b).build();
		Rule r5 = Rule.withHead(C).addSymbol(c).build();
		Rule r6 = Rule.withHead(D).addSymbol(c).build();
		
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).build();
	}
	
	private static ParseSuccess getNewParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(12)
				.setGSSNodesCount(5)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(5)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF(registry), statistics, getInput());
	}
	
	private static ParseSuccess getOriginalParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(14)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(5)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(4)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF(registry), statistics, getInput());
	}

	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3);
		PackedNode node2 = factory.createPackedNode("S ::= A B D .", 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A B D .", 0, 3);
		PackedNode node4 = factory.createPackedNode("S ::= A B D .", 2, node3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= A B . D", 0, 2);
		PackedNode node6 = factory.createPackedNode("S ::= A B . D", 1, node5);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node8 = factory.createPackedNode("A ::= a .", 1, node7);
		TerminalNode node9 = factory.createTerminalNode("a", 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalNode node10 = factory.createNonterminalNode("B", 0, 1, 2);
		PackedNode node11 = factory.createPackedNode("B ::= b .", 2, node10);
		TerminalNode node12 = factory.createTerminalNode("b", 1, 2);
		node11.addChild(node12);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		node5.addChild(node6);
		NonterminalNode node13 = factory.createNonterminalNode("D", 0, 2, 3);
		PackedNode node14 = factory.createPackedNode("D ::= c .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("c", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		node4.addChild(node5);
		node4.addChild(node13);
		node3.addChild(node4);
		node2.addChild(node3);
		PackedNode node16 = factory.createPackedNode("S ::= A B C .", 3, node1);
		IntermediateNode node17 = factory.createIntermediateNode("S ::= A B C .", 0, 3);
		PackedNode node18 = factory.createPackedNode("S ::= A B C .", 2, node17);
		IntermediateNode node19 = factory.createIntermediateNode("S ::= A B . C", 0, 2);
		PackedNode node20 = factory.createPackedNode("S ::= A B . C", 1, node19);
		node20.addChild(node7);
		node20.addChild(node10);
		node19.addChild(node20);
		NonterminalNode node23 = factory.createNonterminalNode("C", 0, 2, 3);
		PackedNode node24 = factory.createPackedNode("C ::= c .", 3, node23);
		node24.addChild(node15);
		node23.addChild(node24);
		node18.addChild(node19);
		node18.addChild(node23);
		node17.addChild(node18);
		node16.addChild(node17);
		node1.addChild(node2);
		node1.addChild(node16);
		return node1;
	}
}
	
