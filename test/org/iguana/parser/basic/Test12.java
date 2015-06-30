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
 * 
 * A ::= A A | a | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test12 extends AbstractParserTest {
	
	static Nonterminal A = Nonterminal.withName("A");
	
	@Parameters
    public static Collection<Object[]> data() {
    	List<Object[]> parameters = 
    		newConfigs.stream().map(c -> new Object[] {
	    		getInput1(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test12::getParseResult1
	    	}).collect(Collectors.toList());
    	parameters.addAll(    		
    		newConfigs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput2(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test12::getParseResult2
	    	}).collect(Collectors.toList()));
    	parameters.addAll(    		
        	originalConfigs.stream().map(c -> new Object[] {
    	    	getInput1(), 
    	    	getGrammar(), 
    	    	getStartSymbol(),
    	    	ParserFactory.getParser(c, getInput2(), getGrammar()),
    	    	(Function<GrammarGraph, ParseResult>) Test12::getParseResult3
    	    	}).collect(Collectors.toList()));
    	parameters.addAll(    		
    		originalConfigs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput2(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test12::getParseResult4
	    	}).collect(Collectors.toList()));
    	return parameters;
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
    
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
    
    private static Input getInput1() {
    	return Input.fromString("a");
    }
    
    private static Input getInput2() {
    	return Input.empty();
    }
    
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(A, A).build();
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(A).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	private static ParseSuccess getParseResult1(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(12)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(5)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(4).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult2(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF2(registry), statistics);
	}
	
	private static ParseSuccess getParseResult3(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(31)
				.setGSSNodesCount(5)
				.setGSSEdgesCount(13)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(10)
				.setAmbiguousNodesCount(4).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult4(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(15)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF2(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF1(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node2 = factory.createPackedNode("A ::= a .", 1, node1);
		TerminalNode node3 = factory.createTerminalNode("a", 0, 1);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 1, node1);
		IntermediateNode node5 = factory.createIntermediateNode("A ::= A A .", 0, 1);
		PackedNode node6 = factory.createPackedNode("A ::= A A .", 1, node5);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 1, 1);
		PackedNode node9 = factory.createPackedNode("A ::= .", 1, node8);
		TerminalNode node10 = factory.createEpsilonNode(1);
		node9.addChild(node10);
		PackedNode node11 = factory.createPackedNode("A ::= A A .", 1, node8);
		IntermediateNode node12 = factory.createIntermediateNode("A ::= A A .", 1, 1);
		PackedNode node13 = factory.createPackedNode("A ::= A A .", 1, node12);
		node13.addChild(node8);
		node13.addChild(node8);
		node12.addChild(node13);
		node11.addChild(node12);
		node8.addChild(node9);
		node8.addChild(node11);
		node6.addChild(node1);
		node6.addChild(node8);
		PackedNode node16 = factory.createPackedNode("A ::= A A .", 0, node5);
		NonterminalNode node17 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node18 = factory.createPackedNode("A ::= .", 0, node17);
		TerminalNode node19 = factory.createEpsilonNode(0);
		node18.addChild(node19);
		PackedNode node20 = factory.createPackedNode("A ::= A A .", 0, node17);
		IntermediateNode node21 = factory.createIntermediateNode("A ::= A A .", 0, 0);
		PackedNode node22 = factory.createPackedNode("A ::= A A .", 0, node21);
		node22.addChild(node17);
		node22.addChild(node17);
		node21.addChild(node22);
		node20.addChild(node21);
		node17.addChild(node18);
		node17.addChild(node20);
		node16.addChild(node17);
		node16.addChild(node1);
		node5.addChild(node16);
		node5.addChild(node16);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node2 = factory.createPackedNode("A ::= .", 0, node1);
		TerminalNode node3 = factory.createEpsilonNode(0);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 0, node1);
		IntermediateNode node5 = factory.createIntermediateNode("A ::= A A .", 0, 0);
		PackedNode node6 = factory.createPackedNode("A ::= A A .", 0, node5);
		node6.addChild(node1);
		node6.addChild(node1);
		node5.addChild(node6);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}
}
