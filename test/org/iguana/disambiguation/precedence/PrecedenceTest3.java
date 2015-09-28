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
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * E ::= E E+    (non-assoc)
 *     > E + E	 (left)
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest3 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal EPlus = new Nonterminal.Builder("E+").setEbnfList(true).build();
	private Character a = Character.from('a');
	private Character plus = Character.from('+');

	private Grammar grammar;
	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E E+
		Rule rule1 = Rule.withHead(E).addSymbols(E, EPlus).build();
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = Rule.withHead(EPlus).addSymbols(EPlus, E).build();
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = Rule.withHead(EPlus).addSymbols(E).build();
		builder.addRule(rule5);
		
		List<PrecedencePattern> precedence = new ArrayList<>();
		
		// (E ::= .E E+, E E+)
		precedence.add(PrecedencePattern.from(rule1, 0, rule1));
		
		// (E ::= E .E+, E E+)
		precedence.add(PrecedencePattern.from(rule1, 1, rule1));
		
		// (E ::= .E E+, E + E) 
		precedence.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E ::= E .E+, E + E)
		precedence.add(PrecedencePattern.from(rule1, 1, rule2));
		
		// (E ::= E + .E, E + E)
		precedence.add(PrecedencePattern.from(rule2, 2, rule2));
		

		List<ExceptPattern> except = new ArrayList<>();
		// (E+ ::= E+ .E, E ::= E+ E)
		except.add(ExceptPattern.from(rule4, 1, rule1));
		except.add(ExceptPattern.from(rule4, 1, rule2));
		except.add(ExceptPattern.from(rule5, 0, rule1));
		except.add(ExceptPattern.from(rule5, 0, rule2));
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(precedence, except);
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
        assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
	@Test
	public void testGrammar() {
		assertEquals(getGrammar(), grammar);
	}
	
	private Grammar getGrammar() {
		return Grammar.builder()
				//E+ ::= E+ E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setEbnfList(true).build()).addSymbol(Nonterminal.builder("E+").setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E+ ::= E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E ::= E1 E+1 
				.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Nonterminal.builder("E+").setIndex(1).setEbnfList(true).build()).build())
				//E ::= E + E2 
				.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Character.from(43)).addSymbol(Nonterminal.builder("E").setIndex(2).build()).build())
				//E ::= a 
				.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Character.from(97)).build())
				//E+2 ::= E+ E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setIndex(2).setEbnfList(true).build()).addSymbol(Nonterminal.builder("E+").setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E+2 ::= E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setIndex(2).setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E+1 ::= E+ E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setIndex(1).setEbnfList(true).build()).addSymbol(Nonterminal.builder("E+").setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E+1 ::= E3 
				.addRule(Rule.withHead(Nonterminal.builder("E+").setIndex(1).setEbnfList(true).build()).addSymbol(Nonterminal.builder("E").setIndex(3).build()).build())
				//E1 ::= a 
				.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Character.from(97)).build())
				//E2 ::= E1 E+2 
				.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Nonterminal.builder("E").setIndex(1).build()).addSymbol(Nonterminal.builder("E+").setIndex(2).setEbnfList(true).build()).build())
				//E2 ::= a 
				.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(2).build()).addSymbol(Character.from(97)).build())
				//E3 ::= a 
				.addRule(Rule.withHead(Nonterminal.builder("E").setIndex(3).build()).addSymbol(Character.from(97)).build())
				.build();
	}
	
//	private NonterminalNode getSPPF(GrammarGraph graph) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 14);
//		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 10, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 10);
//		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 9, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 9);
//		PackedNode node6 = factory.createPackedNode("E ::= E + E2 .", 4, node5);
//		IntermediateNode node7 = factory.createIntermediateNode("E ::= E + . E2", 0, 4);
//		PackedNode node8 = factory.createPackedNode("E ::= E + . E2", 3, node7);
//		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 0, 3);
//		PackedNode node10 = factory.createPackedNode("E ::= E1 E+1 .", 1, node9);
//		NonterminalNode node11 = factory.createNonterminalNode("E", 1, 0, 1);
//		PackedNode node12 = factory.createPackedNode("E1 ::= a .", 1, node11);
//		TerminalNode node13 = factory.createTerminalNode("a", 0, 1);
//		node12.addChild(node13);
//		node11.addChild(node12);
//		NonterminalNode node14 = factory.createNonterminalNode("E+", 1, 1, 3);
//		PackedNode node15 = factory.createPackedNode("E+1 ::= E+ E3 .", 2, node14);
//		NonterminalNode node16 = factory.createNonterminalNode("E+", 0, 1, 2);
//		PackedNode node17 = factory.createPackedNode("E+ ::= E3 .", 2, node16);
//		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 1, 2);
//		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 2, node18);
//		TerminalNode node20 = factory.createTerminalNode("a", 1, 2);
//		node19.addChild(node20);
//		node18.addChild(node19);
//		node17.addChild(node18);
//		node16.addChild(node17);
//		NonterminalNode node21 = factory.createNonterminalNode("E", 3, 2, 3);
//		PackedNode node22 = factory.createPackedNode("E3 ::= a .", 3, node21);
//		TerminalNode node23 = factory.createTerminalNode("a", 2, 3);
//		node22.addChild(node23);
//		node21.addChild(node22);
//		node15.addChild(node16);
//		node15.addChild(node21);
//		node14.addChild(node15);
//		node10.addChild(node11);
//		node10.addChild(node14);
//		node9.addChild(node10);
//		TerminalNode node24 = factory.createTerminalNode("+", 3, 4);
//		node8.addChild(node9);
//		node8.addChild(node24);
//		node7.addChild(node8);
//		NonterminalNode node25 = factory.createNonterminalNode("E", 2, 4, 9);
//		PackedNode node26 = factory.createPackedNode("E2 ::= E1 E+2 .", 5, node25);
//		NonterminalNode node27 = factory.createNonterminalNode("E", 1, 4, 5);
//		PackedNode node28 = factory.createPackedNode("E1 ::= a .", 5, node27);
//		TerminalNode node29 = factory.createTerminalNode("a", 4, 5);
//		node28.addChild(node29);
//		node27.addChild(node28);
//		NonterminalNode node30 = factory.createNonterminalNode("E+", 2, 5, 9);
//		PackedNode node31 = factory.createPackedNode("E+2 ::= E+ E3 .", 8, node30);
//		NonterminalNode node32 = factory.createNonterminalNode("E+", 0, 5, 8);
//		PackedNode node33 = factory.createPackedNode("E+ ::= E+ E3 .", 7, node32);
//		NonterminalNode node34 = factory.createNonterminalNode("E+", 0, 5, 7);
//		PackedNode node35 = factory.createPackedNode("E+ ::= E+ E3 .", 6, node34);
//		NonterminalNode node36 = factory.createNonterminalNode("E+", 0, 5, 6);
//		PackedNode node37 = factory.createPackedNode("E+ ::= E3 .", 6, node36);
//		NonterminalNode node38 = factory.createNonterminalNode("E", 3, 5, 6);
//		PackedNode node39 = factory.createPackedNode("E3 ::= a .", 6, node38);
//		TerminalNode node40 = factory.createTerminalNode("a", 5, 6);
//		node39.addChild(node40);
//		node38.addChild(node39);
//		node37.addChild(node38);
//		node36.addChild(node37);
//		NonterminalNode node41 = factory.createNonterminalNode("E", 3, 6, 7);
//		PackedNode node42 = factory.createPackedNode("E3 ::= a .", 7, node41);
//		TerminalNode node43 = factory.createTerminalNode("a", 6, 7);
//		node42.addChild(node43);
//		node41.addChild(node42);
//		node35.addChild(node36);
//		node35.addChild(node41);
//		node34.addChild(node35);
//		NonterminalNode node44 = factory.createNonterminalNode("E", 3, 7, 8);
//		PackedNode node45 = factory.createPackedNode("E3 ::= a .", 8, node44);
//		TerminalNode node46 = factory.createTerminalNode("a", 7, 8);
//		node45.addChild(node46);
//		node44.addChild(node45);
//		node33.addChild(node34);
//		node33.addChild(node44);
//		node32.addChild(node33);
//		NonterminalNode node47 = factory.createNonterminalNode("E", 3, 8, 9);
//		PackedNode node48 = factory.createPackedNode("E3 ::= a .", 9, node47);
//		TerminalNode node49 = factory.createTerminalNode("a", 8, 9);
//		node48.addChild(node49);
//		node47.addChild(node48);
//		node31.addChild(node32);
//		node31.addChild(node47);
//		node30.addChild(node31);
//		node26.addChild(node27);
//		node26.addChild(node30);
//		node25.addChild(node26);
//		node6.addChild(node7);
//		node6.addChild(node25);
//		node5.addChild(node6);
//		TerminalNode node50 = factory.createTerminalNode("+", 9, 10);
//		node4.addChild(node5);
//		node4.addChild(node50);
//		node3.addChild(node4);
//		NonterminalNode node51 = factory.createNonterminalNode("E", 2, 10, 14);
//		PackedNode node52 = factory.createPackedNode("E2 ::= E1 E+2 .", 11, node51);
//		NonterminalNode node53 = factory.createNonterminalNode("E", 1, 10, 11);
//		PackedNode node54 = factory.createPackedNode("E1 ::= a .", 11, node53);
//		TerminalNode node55 = factory.createTerminalNode("a", 10, 11);
//		node54.addChild(node55);
//		node53.addChild(node54);
//		NonterminalNode node56 = factory.createNonterminalNode("E+", 2, 11, 14);
//		PackedNode node57 = factory.createPackedNode("E+2 ::= E+ E3 .", 13, node56);
//		NonterminalNode node58 = factory.createNonterminalNode("E+", 0, 11, 13);
//		PackedNode node59 = factory.createPackedNode("E+ ::= E+ E3 .", 12, node58);
//		NonterminalNode node60 = factory.createNonterminalNode("E+", 0, 11, 12);
//		PackedNode node61 = factory.createPackedNode("E+ ::= E3 .", 12, node60);
//		NonterminalNode node62 = factory.createNonterminalNode("E", 3, 11, 12);
//		PackedNode node63 = factory.createPackedNode("E3 ::= a .", 12, node62);
//		TerminalNode node64 = factory.createTerminalNode("a", 11, 12);
//		node63.addChild(node64);
//		node62.addChild(node63);
//		node61.addChild(node62);
//		node60.addChild(node61);
//		NonterminalNode node65 = factory.createNonterminalNode("E", 3, 12, 13);
//		PackedNode node66 = factory.createPackedNode("E3 ::= a .", 13, node65);
//		TerminalNode node67 = factory.createTerminalNode("a", 12, 13);
//		node66.addChild(node67);
//		node65.addChild(node66);
//		node59.addChild(node60);
//		node59.addChild(node65);
//		node58.addChild(node59);
//		NonterminalNode node68 = factory.createNonterminalNode("E", 3, 13, 14);
//		PackedNode node69 = factory.createPackedNode("E3 ::= a .", 14, node68);
//		TerminalNode node70 = factory.createTerminalNode("a", 13, 14);
//		node69.addChild(node70);
//		node68.addChild(node69);
//		node57.addChild(node58);
//		node57.addChild(node68);
//		node56.addChild(node57);
//		node52.addChild(node53);
//		node52.addChild(node56);
//		node51.addChild(node52);
//		node2.addChild(node3);
//		node2.addChild(node51);
//		node1.addChild(node2);
//		return node1;
//	}

}
