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
 * A ::=  B 'a' 'c'
 * B ::= 'b'
 * 
 * @author Ali Afroozeh
 */
@RunWith(Parameterized.class)
public class Test13 extends AbstractParserTest {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c = Character.from('c');
	
	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test13::getParseResult
	    	}).collect(Collectors.toList());
    }
    
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(set(B), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(set(), reachabilityGraph.getReachableNonterminals(B));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(B));
	}
    
    private static Input getInput() {
    	return Input.fromString("bac");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
	
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(B, a, c).build();
		Rule r2 = Rule.withHead(B).addSymbol(b).build();
		return Grammar.builder().addRule(r1).addRule(r2).build();
	}
	
	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(3)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(1)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 3);
		PackedNode node2 = factory.createPackedNode("A ::= B a c .", 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B a c .", 0, 3);
		PackedNode node4 = factory.createPackedNode("A ::= B a c .", 2, node3);
		IntermediateNode node5 = factory.createIntermediateNode("A ::= B a . c", 0, 2);
		PackedNode node6 = factory.createPackedNode("A ::= B a . c", 1, node5);
		NonterminalNode node7 = factory.createNonterminalNode("B", 0, 0, 1);
		PackedNode node8 = factory.createPackedNode("B ::= b .", 1, node7);
		TerminalNode node9 = factory.createTerminalNode("b", 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		TerminalNode node10 = factory.createTerminalNode("a", 1, 2);
		node6.addChild(node7);
		node6.addChild(node10);
		node5.addChild(node6);
		TerminalNode node11 = factory.createTerminalNode("c", 2, 3);
		node4.addChild(node5);
		node4.addChild(node11);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
}
