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

package org.iguana.disambiguation.precedence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * E ::= E * E
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest0 {

	private Iguana parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character star = Character.from('*');
	private Character plus = Character.from('+');

	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E * E
		Rule rule1 = Rule.withHead(E).addSymbols(E, star, E).build();
		builder.addRule(rule1);
		
		// E ::= E + E
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbol(a).build();
		builder.addRule(rule3);

		
		List<PrecedencePattern> list = new ArrayList<>();
		
		// (E, E * .E, E * E)
		list.add(PrecedencePattern.from(rule1, 2, rule1));
		
		// (E, E * .E, E + E)
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E, .E * E, E + E)
		list.add(PrecedencePattern.from(rule1, 2, rule2));
		
		// (E, E + .E, E + E)
		list.add(PrecedencePattern.from(rule2, 2, rule2));
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testGrammar() {
		assertEquals(getGrammar(), grammar);
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a");
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode(parser.getGrammarGraph())));
	}
	
	private Grammar getGrammar() {
		return Grammar.builder()
					  .addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(42)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
					  .addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(2).build()).build())
					  .addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(97)).build())
					  .addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(97)).build())
					  .addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(42)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
					  .addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(97)).build())
					  .build();
	}
	
//	private SPPFNode getSPPFNode(GrammarGraph graph) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 5);
//		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 2, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 2);
//		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 1, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 1);
//		PackedNode node6 = factory.createPackedNode("E ::= a .", 1, node5);
//		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
//		node6.addChild(node7);
//		node5.addChild(node6);
//		TerminalNode node8 = factory.createTerminalNode("+", 1, 2);
//		node4.addChild(node5);
//		node4.addChild(node8);
//		node3.addChild(node4);
//		NonterminalNode node9 = factory.createNonterminalNode("E", 2, 2, 5);
//		PackedNode node10 = factory.createPackedNode("E2 ::= E2 * E1 .", 4, node9);
//		IntermediateNode node11 = factory.createIntermediateNode("E2 ::= E2 * . E1", 2, 4);
//		PackedNode node12 = factory.createPackedNode("E2 ::= E2 * . E1", 3, node11);
//		NonterminalNode node13 = factory.createNonterminalNode("E", 2, 2, 3);
//		PackedNode node14 = factory.createPackedNode("E2 ::= a .", 3, node13);
//		TerminalNode node15 = factory.createTerminalNode("a", 2, 3);
//		node14.addChild(node15);
//		node13.addChild(node14);
//		TerminalNode node16 = factory.createTerminalNode("*", 3, 4);
//		node12.addChild(node13);
//		node12.addChild(node16);
//		node11.addChild(node12);
//		NonterminalNode node17 = factory.createNonterminalNode("E", 1, 4, 5);
//		PackedNode node18 = factory.createPackedNode("E1 ::= a .", 5, node17);
//		TerminalNode node19 = factory.createTerminalNode("a", 4, 5);
//		node18.addChild(node19);
//		node17.addChild(node18);
//		node10.addChild(node11);
//		node10.addChild(node17);
//		node9.addChild(node10);
//		node2.addChild(node3);
//		node2.addChild(node9);
//		node1.addChild(node2);
//		return node1;
//	}

}
