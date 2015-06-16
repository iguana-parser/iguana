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
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(3).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult2(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(2)
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
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(3).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult4(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(15)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(2)
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
		NonterminalNode node6 = factory.createNonterminalNode("A", 0, 1, 1);
		PackedNode node7 = factory.createPackedNode("A ::= .", 1, node6);
		TerminalNode node8 = factory.createEpsilonNode(1);
		node7.addChild(node8);
		PackedNode node9 = factory.createPackedNode("A ::= A A .", 1, node6);
		node9.addChild(node6);
		node9.addChild(node6);
		node6.addChild(node7);
		node6.addChild(node9);
		node4.addChild(node1);
		node4.addChild(node6);
		PackedNode node12 = factory.createPackedNode("A ::= A A .", 0, node1);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node14 = factory.createPackedNode("A ::= .", 0, node13);
		TerminalNode node15 = factory.createEpsilonNode(0);
		node14.addChild(node15);
		PackedNode node16 = factory.createPackedNode("A ::= A A .", 0, node13);
		node16.addChild(node13);
		node16.addChild(node13);
		node13.addChild(node14);
		node13.addChild(node16);
		node12.addChild(node13);
		node12.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node4);
		node1.addChild(node12);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF2(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node2 = factory.createPackedNode("A ::= .", 0, node1);
		TerminalNode node3 = factory.createEpsilonNode(0);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 0, node1);
		node4.addChild(node1);
		node4.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}
}
