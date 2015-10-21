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
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * E ::= EPlus E     (non-assoc)
 *     > E + E	  (left)
 *     | a
 * 
 * EPlus ::= EPlus E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest7 {

	private Iguana parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal EPlus = new Nonterminal.Builder("EPlus").setEbnfList(true).build();
	private Character a = Character.from('a');
	private Character plus = Character.from('+');

	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= EPlus E
		Rule rule1 = Rule.withHead(E).addSymbols(EPlus, E).build();
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		// EPlus ::= EPlus E
		Rule rule4 = Rule.withHead(EPlus).addSymbols(EPlus, E).build();
		builder.addRule(rule4);
		
		// EPlus ::= E
		Rule rule5 = Rule.withHead(EPlus).addSymbols(E).build();
		builder.addRule(rule5);
		
		
		List<PrecedencePattern> precedence = new ArrayList<>();
		
		// (E ::= .EPlus E, EPlus E) 
		precedence.add(PrecedencePattern.from(rule1, 0, rule1));
		
		// (E ::= EPlus .E, EPlus E)
		precedence.add(PrecedencePattern.from(rule1, 1, rule1));
		
		// (E ::= .EPlus E, E + E) 
		precedence.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E ::= EPlus .E, E + E)
		precedence.add(PrecedencePattern.from(rule1, 1, rule2));
		
		// (E ::= E + .E, E + E)
		precedence.add(PrecedencePattern.from(rule2, 2, rule2));
		
		
		List<ExceptPattern> except = new ArrayList<>();
		
		except.add(ExceptPattern.from(rule4, 1, rule1));
		except.add(ExceptPattern.from(rule4, 1, rule2));
		except.add(ExceptPattern.from(rule5, 0, rule1));
		except.add(ExceptPattern.from(rule5, 0, rule2));
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(precedence, except);
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aaa+aaaa+aaaa");
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
//	private NonterminalNode getSPPF(GrammarGraph graph) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 13);
//		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 9, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 9);
//		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 8, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 8);
//		PackedNode node6 = factory.createPackedNode("E ::= E + E2 .", 4, node5);
//		IntermediateNode node7 = factory.createIntermediateNode("E ::= E + . E2", 0, 4);
//		PackedNode node8 = factory.createPackedNode("E ::= E + . E2", 3, node7);
//		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 0, 3);
//		PackedNode node10 = factory.createPackedNode("E ::= EPlus1 E1 .", 2, node9);
//		NonterminalNode node11 = factory.createNonterminalNode("EPlus", 1, 0, 2);
//		PackedNode node12 = factory.createPackedNode("EPlus1 ::= EPlus1 E3 .", 1, node11);
//		NonterminalNode node13 = factory.createNonterminalNode("EPlus", 1, 0, 1);
//		PackedNode node14 = factory.createPackedNode("EPlus1 ::= E3 .", 1, node13);
//		NonterminalNode node15 = factory.createNonterminalNode("E", 3, 0, 1);
//		PackedNode node16 = factory.createPackedNode("E3 ::= a .", 1, node15);
//		TerminalNode node17 = factory.createTerminalNode("a", 0, 1);
//		node16.addChild(node17);
//		node15.addChild(node16);
//		node14.addChild(node15);
//		node13.addChild(node14);
//		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 1, 2);
//		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 2, node18);
//		TerminalNode node20 = factory.createTerminalNode("a", 1, 2);
//		node19.addChild(node20);
//		node18.addChild(node19);
//		node12.addChild(node13);
//		node12.addChild(node18);
//		node11.addChild(node12);
//		NonterminalNode node21 = factory.createNonterminalNode("E", 1, 2, 3);
//		PackedNode node22 = factory.createPackedNode("E1 ::= a .", 3, node21);
//		TerminalNode node23 = factory.createTerminalNode("a", 2, 3);
//		node22.addChild(node23);
//		node21.addChild(node22);
//		node10.addChild(node11);
//		node10.addChild(node21);
//		node9.addChild(node10);
//		TerminalNode node24 = factory.createTerminalNode("+", 3, 4);
//		node8.addChild(node9);
//		node8.addChild(node24);
//		node7.addChild(node8);
//		NonterminalNode node25 = factory.createNonterminalNode("E", 2, 4, 8);
//		PackedNode node26 = factory.createPackedNode("E2 ::= EPlus2 E1 .", 7, node25);
//		NonterminalNode node27 = factory.createNonterminalNode("EPlus", 2, 4, 7);
//		PackedNode node28 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 6, node27);
//		NonterminalNode node29 = factory.createNonterminalNode("EPlus", 2, 4, 6);
//		PackedNode node30 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 5, node29);
//		NonterminalNode node31 = factory.createNonterminalNode("EPlus", 2, 4, 5);
//		PackedNode node32 = factory.createPackedNode("EPlus2 ::= E3 .", 5, node31);
//		NonterminalNode node33 = factory.createNonterminalNode("E", 3, 4, 5);
//		PackedNode node34 = factory.createPackedNode("E3 ::= a .", 5, node33);
//		TerminalNode node35 = factory.createTerminalNode("a", 4, 5);
//		node34.addChild(node35);
//		node33.addChild(node34);
//		node32.addChild(node33);
//		node31.addChild(node32);
//		NonterminalNode node36 = factory.createNonterminalNode("E", 3, 5, 6);
//		PackedNode node37 = factory.createPackedNode("E3 ::= a .", 6, node36);
//		TerminalNode node38 = factory.createTerminalNode("a", 5, 6);
//		node37.addChild(node38);
//		node36.addChild(node37);
//		node30.addChild(node31);
//		node30.addChild(node36);
//		node29.addChild(node30);
//		NonterminalNode node39 = factory.createNonterminalNode("E", 3, 6, 7);
//		PackedNode node40 = factory.createPackedNode("E3 ::= a .", 7, node39);
//		TerminalNode node41 = factory.createTerminalNode("a", 6, 7);
//		node40.addChild(node41);
//		node39.addChild(node40);
//		node28.addChild(node29);
//		node28.addChild(node39);
//		node27.addChild(node28);
//		NonterminalNode node42 = factory.createNonterminalNode("E", 1, 7, 8);
//		PackedNode node43 = factory.createPackedNode("E1 ::= a .", 8, node42);
//		TerminalNode node44 = factory.createTerminalNode("a", 7, 8);
//		node43.addChild(node44);
//		node42.addChild(node43);
//		node26.addChild(node27);
//		node26.addChild(node42);
//		node25.addChild(node26);
//		node6.addChild(node7);
//		node6.addChild(node25);
//		node5.addChild(node6);
//		TerminalNode node45 = factory.createTerminalNode("+", 8, 9);
//		node4.addChild(node5);
//		node4.addChild(node45);
//		node3.addChild(node4);
//		NonterminalNode node46 = factory.createNonterminalNode("E", 2, 9, 13);
//		PackedNode node47 = factory.createPackedNode("E2 ::= EPlus2 E1 .", 12, node46);
//		NonterminalNode node48 = factory.createNonterminalNode("EPlus", 2, 9, 12);
//		PackedNode node49 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 11, node48);
//		NonterminalNode node50 = factory.createNonterminalNode("EPlus", 2, 9, 11);
//		PackedNode node51 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 10, node50);
//		NonterminalNode node52 = factory.createNonterminalNode("EPlus", 2, 9, 10);
//		PackedNode node53 = factory.createPackedNode("EPlus2 ::= E3 .", 10, node52);
//		NonterminalNode node54 = factory.createNonterminalNode("E", 3, 9, 10);
//		PackedNode node55 = factory.createPackedNode("E3 ::= a .", 10, node54);
//		TerminalNode node56 = factory.createTerminalNode("a", 9, 10);
//		node55.addChild(node56);
//		node54.addChild(node55);
//		node53.addChild(node54);
//		node52.addChild(node53);
//		NonterminalNode node57 = factory.createNonterminalNode("E", 3, 10, 11);
//		PackedNode node58 = factory.createPackedNode("E3 ::= a .", 11, node57);
//		TerminalNode node59 = factory.createTerminalNode("a", 10, 11);
//		node58.addChild(node59);
//		node57.addChild(node58);
//		node51.addChild(node52);
//		node51.addChild(node57);
//		node50.addChild(node51);
//		NonterminalNode node60 = factory.createNonterminalNode("E", 3, 11, 12);
//		PackedNode node61 = factory.createPackedNode("E3 ::= a .", 12, node60);
//		TerminalNode node62 = factory.createTerminalNode("a", 11, 12);
//		node61.addChild(node62);
//		node60.addChild(node61);
//		node49.addChild(node50);
//		node49.addChild(node60);
//		node48.addChild(node49);
//		NonterminalNode node63 = factory.createNonterminalNode("E", 1, 12, 13);
//		PackedNode node64 = factory.createPackedNode("E1 ::= a .", 13, node63);
//		TerminalNode node65 = factory.createTerminalNode("a", 12, 13);
//		node64.addChild(node65);
//		node63.addChild(node64);
//		node47.addChild(node48);
//		node47.addChild(node63);
//		node46.addChild(node47);
//		node2.addChild(node3);
//		node2.addChild(node46);
//		node1.addChild(node2);
//		return node1;
//	}

}
