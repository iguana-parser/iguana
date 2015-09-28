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
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * E ::= E Y    (none)
 * 	   > E ; E  (right)
 * 	   > - E
 *     | a
 * 
 * Y ::= X
 * 
 * X ::= X , E
 *     | , E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest11 {
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal X = Nonterminal.withName("X");
	private Nonterminal Y = Nonterminal.withName("Y");
	private Character a = Character.from('a');
	private Character comma = Character.from(',');
	private Character semicolon = Character.from(';');
	private Character min = Character.from('-');
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E Y
		Rule rule1 = Rule.withHead(E).addSymbols(E, Y).build();
		builder.addRule(rule1);
		
		// E ::= E ; E
		Rule rule2 = Rule.withHead(E).addSymbols(E, semicolon, E).build();
		builder.addRule(rule2);
		
		// E ::= - E
		Rule rule3 = Rule.withHead(E).addSymbols(min, E).build();
		builder.addRule(rule3);
		
		// E ::= a
		Rule rule4 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule4);
		
		// Y ::= X
		Rule rule5 = Rule.withHead(Y).addSymbols(X).build();
		builder.addRule(rule5);
		
		// X ::= X , E
		Rule rule6 = Rule.withHead(X).addSymbols(X, comma, E).build();
		builder.addRule(rule6);
		
		// X ::= , E
		Rule rule7 = Rule.withHead(X).addSymbols(comma, E).build();
		builder.addRule(rule7);
		

		List<PrecedencePattern> list = new ArrayList<>();
		
		// (E, .E Y, E ";" E)
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E, E .Y, E ";" E)
		list.add(PrecedencePattern.from(rule1, 1, rule2));
		
		// (E, .E Y, - E)
		list.add(PrecedencePattern.from(rule1, 0, rule3));		
		
		// (E, .E ";" E, - E)
		list.add(PrecedencePattern.from(rule2, 0, rule3));
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);
	
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("a,-a;a");
		parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
//	public SPPFNode getSPPF(GrammarGraph grammar) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(grammar);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 6);
//		PackedNode node2 = factory.createPackedNode("E ::= E1 Y1 .", 1, node1);
//		NonterminalNode node3 = factory.createNonterminalNode("E", 1, 0, 1);
//		PackedNode node4 = factory.createPackedNode("E1 ::= a .", 1, node3);
//		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
//		node4.addChild(node5);
//		node3.addChild(node4);
//		NonterminalNode node6 = factory.createNonterminalNode("Y", 1, 1, 6);
//		PackedNode node7 = factory.createPackedNode("Y1 ::= X1 .", 6, node6);
//		NonterminalNode node8 = factory.createNonterminalNode("X", 1, 1, 6);
//		PackedNode node9 = factory.createPackedNode("X1 ::= , E2 .", 2, node8);
//		TerminalNode node10 = factory.createTerminalNode(",", 1, 2);
//		NonterminalNode node11 = factory.createNonterminalNode("E", 2, 2, 6);
//		PackedNode node12 = factory.createPackedNode("E2 ::= - E .", 3, node11);
//		TerminalNode node13 = factory.createTerminalNode("-", 2, 3);
//		NonterminalNode node14 = factory.createNonterminalNode("E", 0, 3, 6);
//		PackedNode node15 = factory.createPackedNode("E ::= E3 ; E .", 5, node14);
//		IntermediateNode node16 = factory.createIntermediateNode("E ::= E3 ; . E", 3, 5);
//		PackedNode node17 = factory.createPackedNode("E ::= E3 ; . E", 4, node16);
//		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 3, 4);
//		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 4, node18);
//		TerminalNode node20 = factory.createTerminalNode("a", 3, 4);
//		node19.addChild(node20);
//		node18.addChild(node19);
//		TerminalNode node21 = factory.createTerminalNode(";", 4, 5);
//		node17.addChild(node18);
//		node17.addChild(node21);
//		node16.addChild(node17);
//		NonterminalNode node22 = factory.createNonterminalNode("E", 0, 5, 6);
//		PackedNode node23 = factory.createPackedNode("E ::= a .", 6, node22);
//		TerminalNode node24 = factory.createTerminalNode("a", 5, 6);
//		node23.addChild(node24);
//		node22.addChild(node23);
//		node15.addChild(node16);
//		node15.addChild(node22);
//		node14.addChild(node15);
//		node12.addChild(node13);
//		node12.addChild(node14);
//		node11.addChild(node12);
//		node9.addChild(node10);
//		node9.addChild(node11);
//		node8.addChild(node9);
//		node7.addChild(node8);
//		node6.addChild(node7);
//		node2.addChild(node3);
//		node2.addChild(node6);
//		node1.addChild(node2);
//		return node1;
//	}

}
