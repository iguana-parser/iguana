/*
 * Copyright (c) 2015, CWI
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

import java.util.Arrays;
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
 * E ::= E + E
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest1 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');
	private Character a = Character.from('a');

	private Grammar grammar;
	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E + E
		Rule rule1 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = Rule.withHead(E).addSymbols(minus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbol(a).build();
		builder.addRule(rule3);
		
		List<PrecedencePattern> list = Arrays.asList(PrecedencePattern.from(rule1, 2, rule1), 
													 PrecedencePattern.from(rule1, 0, rule2));
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testGrammar() {
		assertEquals(getGrammar(), grammar);
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+-a+a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
        assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode(parser.getGrammarGraph())));
	}
	
	private Grammar getGrammar() {
		return Grammar.builder()
		//E ::= E2 + E1 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(1).build()).build())
		//E ::= - E 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(45)).addSymbol(Nonterminal.builder("E").build()).build())
		//E ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(97)).build())
		//E1 ::= - E 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(45)).addSymbol(Nonterminal.builder("E").build()).build())
		//E1 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(97)).build())
		//E2 ::= E2 + E3 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
		//E2 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(97)).build())
		//E3 ::= a 
		.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(97)).build())
		.build();
	}
	
	private SPPFNode getSPPFNode(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 6);
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
		NonterminalNode node9 = factory.createNonterminalNode("E", 1, 2, 6);
		PackedNode node10 = factory.createPackedNode("E1 ::= - E .", 3, node9);
		TerminalNode node11 = factory.createTerminalNode("-", 2, 3);
		NonterminalNode node12 = factory.createNonterminalNode("E", 0, 3, 6);
		PackedNode node13 = factory.createPackedNode("E ::= E2 + E1 .", 5, node12);
		IntermediateNode node14 = factory.createIntermediateNode("E ::= E2 + . E1", 3, 5);
		PackedNode node15 = factory.createPackedNode("E ::= E2 + . E1", 4, node14);
		NonterminalNode node16 = factory.createNonterminalNode("E", 2, 3, 4);
		PackedNode node17 = factory.createPackedNode("E2 ::= a .", 4, node16);
		TerminalNode node18 = factory.createTerminalNode("a", 3, 4);
		node17.addChild(node18);
		node16.addChild(node17);
		TerminalNode node19 = factory.createTerminalNode("+", 4, 5);
		node15.addChild(node16);
		node15.addChild(node19);
		node14.addChild(node15);
		NonterminalNode node20 = factory.createNonterminalNode("E", 1, 5, 6);
		PackedNode node21 = factory.createPackedNode("E1 ::= a .", 6, node20);
		TerminalNode node22 = factory.createTerminalNode("a", 5, 6);
		node21.addChild(node22);
		node20.addChild(node21);
		node13.addChild(node14);
		node13.addChild(node20);
		node12.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node12);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
