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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E ^ E	(right)
 *     > E + E	(left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest2 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character hat = Character.from('^');
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');

	private Grammar grammar;

	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E ^ E
		Rule rule0 = Rule.withHead(E).addSymbols(E, hat, E).build();
		builder.addRule(rule0);
		
		// E ::= E + E
		Rule rule1 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule1);
		
		// E ::= E - E
		Rule rule2 = Rule.withHead(E).addSymbols(minus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		List<PrecedencePattern> list = new ArrayList<>();
		
		// left associative E + E
		list.add(PrecedencePattern.from(rule1, 2, rule1));
		
		// + has higher priority than -
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// right associative E ^ E
		list.add(PrecedencePattern.from(rule0, 0, rule0));
		
		// ^ has higher priority than -
		list.add(PrecedencePattern.from(rule0, 0, rule2));
		
		// ^ has higher priority than +
		list.add(PrecedencePattern.from(rule0, 0, rule1));
		list.add(PrecedencePattern.from(rule0, 2, rule1));
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);

		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testGrammar() {
		assertEquals(getGrammar(), grammar);
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("a+a^a^-a+a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
	private Grammar getGrammar() {
		return Grammar.builder()
		//E4 ::= E3 ^ E4 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(4).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(94)).addSymbol(Nonterminal.builder("E").setIndex(4).build()).build())
		//E4 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(4).build()).addSymbol(Character.from(97)).build())
		//E ::= E3 ^ E1 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(94)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
		//E ::= E2 + E1 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
		//E ::= - E 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(45)).addSymbol(Nonterminal.builder("E").build()).build())
		//E ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(97)).build())
		//E1 ::= E3 ^ E1 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(94)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
		//E1 ::= - E 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(45)).addSymbol(Nonterminal.builder("E").build()).build())
		//E1 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(97)).build())
		//E2 ::= E3 ^ E4 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(94)).addSymbol(Nonterminal.builder("E").setIndex(4).build()).build())
		//E2 ::= E2 + E4 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(4).build()).build())
		//E2 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(97)).build())
		//E3 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(97)).build())
		.build();
	}
	
	private SPPFNode getSPPF(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 10);
		PackedNode node2 = factory.createPackedNode("E ::= E2 + E1 .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E2 + . E1", 0, 2);
		PackedNode node4 = factory.createPackedNode("E ::= E2 + . E1", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 2, 0, 1);
		PackedNode node6 = factory.createPackedNode("E2 ::= a .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		TerminalNode node8 = factory.createTerminalNode("+", 1, 2);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node9 = factory.createNonterminalNode("E", 1, 2, 10);
		PackedNode node10 = factory.createPackedNode("E1 ::= E3 ^ E1 .", 4, node9);
		IntermediateNode node11 = factory.createIntermediateNode("E1 ::= E3 ^ . E1", 2, 4);
		PackedNode node12 = factory.createPackedNode("E1 ::= E3 ^ . E1", 3, node11);
		NonterminalNode node13 = factory.createNonterminalNode("E", 3, 2, 3);
		PackedNode node14 = factory.createPackedNode("E3 ::= a .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("a", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node16 = factory.createTerminalNode("^", 3, 4);
		node12.addChild(node13);
		node12.addChild(node16);
		node11.addChild(node12);
		NonterminalNode node17 = factory.createNonterminalNode("E", 1, 4, 10);
		PackedNode node18 = factory.createPackedNode("E1 ::= E3 ^ E1 .", 6, node17);
		IntermediateNode node19 = factory.createIntermediateNode("E1 ::= E3 ^ . E1", 4, 6);
		PackedNode node20 = factory.createPackedNode("E1 ::= E3 ^ . E1", 5, node19);
		NonterminalNode node21 = factory.createNonterminalNode("E", 3, 4, 5);
		PackedNode node22 = factory.createPackedNode("E3 ::= a .", 5, node21);
		TerminalNode node23 = factory.createTerminalNode("a", 4, 5);
		node22.addChild(node23);
		node21.addChild(node22);
		TerminalNode node24 = factory.createTerminalNode("^", 5, 6);
		node20.addChild(node21);
		node20.addChild(node24);
		node19.addChild(node20);
		NonterminalNode node25 = factory.createNonterminalNode("E", 1, 6, 10);
		PackedNode node26 = factory.createPackedNode("E1 ::= - E .", 7, node25);
		TerminalNode node27 = factory.createTerminalNode("-", 6, 7);
		NonterminalNode node28 = factory.createNonterminalNode("E", 0, 7, 10);
		PackedNode node29 = factory.createPackedNode("E ::= E2 + E1 .", 9, node28);
		IntermediateNode node30 = factory.createIntermediateNode("E ::= E2 + . E1", 7, 9);
		PackedNode node31 = factory.createPackedNode("E ::= E2 + . E1", 8, node30);
		NonterminalNode node32 = factory.createNonterminalNode("E", 2, 7, 8);
		PackedNode node33 = factory.createPackedNode("E2 ::= a .", 8, node32);
		TerminalNode node34 = factory.createTerminalNode("a", 7, 8);
		node33.addChild(node34);
		node32.addChild(node33);
		TerminalNode node35 = factory.createTerminalNode("+", 8, 9);
		node31.addChild(node32);
		node31.addChild(node35);
		node30.addChild(node31);
		NonterminalNode node36 = factory.createNonterminalNode("E", 1, 9, 10);
		PackedNode node37 = factory.createPackedNode("E1 ::= a .", 10, node36);
		TerminalNode node38 = factory.createTerminalNode("a", 9, 10);
		node37.addChild(node38);
		node36.addChild(node37);
		node29.addChild(node30);
		node29.addChild(node36);
		node28.addChild(node29);
		node26.addChild(node27);
		node26.addChild(node28);
		node25.addChild(node26);
		node18.addChild(node19);
		node18.addChild(node25);
		node17.addChild(node18);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
