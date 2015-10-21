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
import org.iguana.grammar.patterns.ExceptPattern;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.GLLParserImpl;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * E ::= E [ E ]
 * 	   | E +
 * 	   | E *
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest8 {
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character plus = Character.from('+');
	private Character star = Character.from('*');
	private Character ob = Character.from('[');
	private Character cb = Character.from(']');
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E [ E ]
		Rule rule1 = Rule.withHead(E).addSymbols(E, ob, E, cb).build();
		builder.addRule(rule1);
		
		// E ::= E +
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus).build();
		builder.addRule(rule2);
		
		// E ::= E *
		Rule rule3 = Rule.withHead(E).addSymbols(E, star).build();
		builder.addRule(rule3);
		
		// E ::= E + E
		Rule rule4 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule5);
		
		
		List<PrecedencePattern> precedence = new ArrayList<>();
		
		// (E, .E [ E ], E + E)
		precedence.add(PrecedencePattern.from(rule1, 0, rule4));
		
		// (E, .E *, E + E)
		precedence.add(PrecedencePattern.from(rule2, 0, rule4));
		
		// (E, .E +, E + E)
		precedence.add(PrecedencePattern.from(rule3, 0, rule4));

		
		List<ExceptPattern> except = new ArrayList<>();
		except.add(ExceptPattern.from(rule1, 0, rule1));
		except.add(ExceptPattern.from(rule1, 0, rule2));
		except.add(ExceptPattern.from(rule1, 0, rule3));

		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(precedence, except);
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("a+a[a+a]");
		parser = new GLLParserImpl();
		ParseResult result = parser.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF1(parser.getGrammarGraph())));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("a+a*a+[a+a]");
		parser = new GLLParserImpl();
		ParseResult result = parser.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("a[a][a+a]");
		parser = new GLLParserImpl();
		ParseResult result = parser.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseError());
	}	

//	private SPPFNode getSPPF1(GrammarGraph graph) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 8);
//		PackedNode node2 = factory.createPackedNode("E ::= E + E .", 2, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", 0, 2);
//		PackedNode node4 = factory.createPackedNode("E ::= E + . E", 1, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 1);
//		PackedNode node6 = factory.createPackedNode("E ::= a .", 1, node5);
//		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
//		node6.addChild(node7);
//		node5.addChild(node6);
//		TerminalNode node8 = factory.createTerminalNode("+", 1, 2);
//		node4.addChild(node5);
//		node4.addChild(node8);
//		node3.addChild(node4);
//		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 2, 8);
//		PackedNode node10 = factory.createPackedNode("E ::= E2 [ E ] .", 7, node9);
//		IntermediateNode node11 = factory.createIntermediateNode("E ::= E2 [ E . ]", 2, 7);
//		PackedNode node12 = factory.createPackedNode("E ::= E2 [ E . ]", 4, node11);
//		IntermediateNode node13 = factory.createIntermediateNode("E ::= E2 [ . E ]", 2, 4);
//		PackedNode node14 = factory.createPackedNode("E ::= E2 [ . E ]", 3, node13);
//		NonterminalNode node15 = factory.createNonterminalNode("E", 2, 2, 3);
//		PackedNode node16 = factory.createPackedNode("E2 ::= a .", 3, node15);
//		TerminalNode node17 = factory.createTerminalNode("a", 2, 3);
//		node16.addChild(node17);
//		node15.addChild(node16);
//		TerminalNode node18 = factory.createTerminalNode("[", 3, 4);
//		node14.addChild(node15);
//		node14.addChild(node18);
//		node13.addChild(node14);
//		NonterminalNode node19 = factory.createNonterminalNode("E", 0, 4, 7);
//		PackedNode node20 = factory.createPackedNode("E ::= E + E .", 6, node19);
//		IntermediateNode node21 = factory.createIntermediateNode("E ::= E + . E", 4, 6);
//		PackedNode node22 = factory.createPackedNode("E ::= E + . E", 5, node21);
//		NonterminalNode node23 = factory.createNonterminalNode("E", 0, 4, 5);
//		PackedNode node24 = factory.createPackedNode("E ::= a .", 5, node23);
//		TerminalNode node25 = factory.createTerminalNode("a", 4, 5);
//		node24.addChild(node25);
//		node23.addChild(node24);
//		TerminalNode node26 = factory.createTerminalNode("+", 5, 6);
//		node22.addChild(node23);
//		node22.addChild(node26);
//		node21.addChild(node22);
//		NonterminalNode node27 = factory.createNonterminalNode("E", 0, 6, 7);
//		PackedNode node28 = factory.createPackedNode("E ::= a .", 7, node27);
//		TerminalNode node29 = factory.createTerminalNode("a", 6, 7);
//		node28.addChild(node29);
//		node27.addChild(node28);
//		node20.addChild(node21);
//		node20.addChild(node27);
//		node19.addChild(node20);
//		node12.addChild(node13);
//		node12.addChild(node19);
//		node11.addChild(node12);
//		TerminalNode node30 = factory.createTerminalNode("]", 7, 8);
//		node10.addChild(node11);
//		node10.addChild(node30);
//		node9.addChild(node10);
//		node2.addChild(node3);
//		node2.addChild(node9);
//		node1.addChild(node2);
//		return node1;
//	}

}
