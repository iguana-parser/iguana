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
 * E ::= E z
 *     > x E
 *     > E w
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest4 {
	
	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character w = Character.from('w');
	private Character x = Character.from('x');
	private Character z = Character.from('z');

	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E z
		Rule rule1 = Rule.withHead(E).addSymbols(E, z).build();
		builder.addRule(rule1);
		
		// E ::=  x E
		Rule rule2 = Rule.withHead(E).addSymbols(x, E).build();
		builder.addRule(rule2);
		
		// E ::= E w
		Rule rule3 = Rule.withHead(E).addSymbols(E, w).build();
		builder.addRule(rule3);
		
		// E ::= a
		Rule rule4 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule4);
		
		List<PrecedencePattern> list = new ArrayList<>();
		
		// (E, .E z, x E) 
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E, x .E, E w)
		list.add(PrecedencePattern.from(rule2, 1, rule3));
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void testAssociativityAndPriority() {
		Input input = Input.fromString("xawz");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
	private SPPFNode getSPPF(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 4);
		PackedNode node2 = factory.createPackedNode("E ::= E1 z .", 3, node1);
		NonterminalNode node3 = factory.createNonterminalNode("E", 1, 0, 3);
		PackedNode node4 = factory.createPackedNode("E1 ::= E w .", 2, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 2);
		PackedNode node6 = factory.createPackedNode("E ::= x E2 .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("x", 0, 1);
		NonterminalNode node8 = factory.createNonterminalNode("E", 2, 1, 2);
		PackedNode node9 = factory.createPackedNode("E2 ::= a .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("a", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		TerminalNode node11 = factory.createTerminalNode("w", 2, 3);
		node4.addChild(node5);
		node4.addChild(node11);
		node3.addChild(node4);
		TerminalNode node12 = factory.createTerminalNode("z", 3, 4);
		node2.addChild(node3);
		node2.addChild(node12);
		node1.addChild(node2);
		return node1;
	}

}
