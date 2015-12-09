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

package org.iguana.parser;

import static org.junit.Assert.assertTrue;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class LeftFactorizedArithmeticGrammarTest {

	private Grammar grammar;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal T = Nonterminal.withName("T");
	private Nonterminal E1 = Nonterminal.withName("E1");
	private Nonterminal F = Nonterminal.withName("F");
	private Nonterminal T1 = Nonterminal.withName("T1");
	private Terminal plus = Terminal.from(Character.from('+'));
	private Terminal star = Terminal.from(Character.from('*'));
	private Terminal a = Terminal.from(Character.from('a'));
	private Terminal openPar = Terminal.from(Character.from('('));
	private Terminal closePar = Terminal.from(Character.from(')'));

	@Before
	public void createGrammar() {

		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = Rule.withHead(E).addSymbols(T, E1).build();
		Rule r2 = Rule.withHead(E1).addSymbols(plus, T, E1).build();
		Rule r3 = Rule.withHead(E1).build();
		Rule r4 = Rule.withHead(T).addSymbols(F, T1).build();
		Rule r5 = Rule.withHead(T1).addSymbols(star, F, T1).build();
		Rule r6 = Rule.withHead(T1).build();
		Rule r7 = Rule.withHead(F).addSymbols(openPar, E, closePar).build();
		Rule r8 = Rule.withHead(F).addSymbol(a).build();
		
		grammar = builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8).build();
	}
	
//	@Test
//	public void testFirstSets() {
//		assertEquals(set(openPar, a), grammar.getFirstSet(E));
//		assertEquals(set(plus, Epsilon.getInstance()), grammar.getFirstSet(E1));
//		assertEquals(set(star, Epsilon.getInstance()), grammar.getFirstSet(T1));
//		assertEquals(set(openPar, a), grammar.getFirstSet(T));
//		assertEquals(set(openPar, a), grammar.getFirstSet(F));
//	}
//	
//	public void testFollowSets() {
//		assertEquals(set(closePar, EOF.getInstance()), grammar.getFollowSet(E));
//		assertEquals(set(closePar, EOF.getInstance()), grammar.getFollowSet(E1));
//		assertEquals(set(plus, closePar, EOF.getInstance()), grammar.getFollowSet(T1));
//		assertEquals(set(plus, closePar, EOF.getInstance()), grammar.getFollowSet(T));
//		assertEquals(set(plus, star, closePar, EOF.getInstance()), grammar.getFollowSet(F));
//	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a+a");
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
//	private SPPFNode getSPPF(GrammarGraph registry) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
//		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 7);
//		PackedNode node2 = factory.createPackedNode("E ::= T E1 .", 1, node1);
//		NonterminalNode node3 = factory.createNonterminalNode("T", 0, 0, 1);
//		PackedNode node4 = factory.createPackedNode("T ::= F T1 .", 1, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("F", 0, 0, 1);
//		PackedNode node6 = factory.createPackedNode("F ::= a .", 1, node5);
//		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
//		node6.addChild(node7);
//		node5.addChild(node6);
//		NonterminalNode node8 = factory.createNonterminalNode("T1", 0, 1, 1);
//		PackedNode node9 = factory.createPackedNode("T1 ::= .", 1, node8);
//		TerminalNode node10 = factory.createEpsilonNode(1);
//		node9.addChild(node10);
//		node8.addChild(node9);
//		node4.addChild(node5);
//		node4.addChild(node8);
//		node3.addChild(node4);
//		NonterminalNode node11 = factory.createNonterminalNode("E1", 0, 1, 7);
//		PackedNode node12 = factory.createPackedNode("E1 ::= + T E1 .", 5, node11);
//		IntermediateNode node13 = factory.createIntermediateNode("E1 ::= + T . E1", 1, 5);
//		PackedNode node14 = factory.createPackedNode("E1 ::= + T . E1", 2, node13);
//		TerminalNode node15 = factory.createTerminalNode("+", 1, 2);
//		NonterminalNode node16 = factory.createNonterminalNode("T", 0, 2, 5);
//		PackedNode node17 = factory.createPackedNode("T ::= F T1 .", 3, node16);
//		NonterminalNode node18 = factory.createNonterminalNode("F", 0, 2, 3);
//		PackedNode node19 = factory.createPackedNode("F ::= a .", 3, node18);
//		TerminalNode node20 = factory.createTerminalNode("a", 2, 3);
//		node19.addChild(node20);
//		node18.addChild(node19);
//		NonterminalNode node21 = factory.createNonterminalNode("T1", 0, 3, 5);
//		PackedNode node22 = factory.createPackedNode("T1 ::= * F T1 .", 5, node21);
//		IntermediateNode node23 = factory.createIntermediateNode("T1 ::= * F . T1", 3, 5);
//		PackedNode node24 = factory.createPackedNode("T1 ::= * F . T1", 4, node23);
//		TerminalNode node25 = factory.createTerminalNode("*", 3, 4);
//		NonterminalNode node26 = factory.createNonterminalNode("F", 0, 4, 5);
//		PackedNode node27 = factory.createPackedNode("F ::= a .", 5, node26);
//		TerminalNode node28 = factory.createTerminalNode("a", 4, 5);
//		node27.addChild(node28);
//		node26.addChild(node27);
//		node24.addChild(node25);
//		node24.addChild(node26);
//		node23.addChild(node24);
//		NonterminalNode node29 = factory.createNonterminalNode("T1", 0, 5, 5);
//		PackedNode node30 = factory.createPackedNode("T1 ::= .", 5, node29);
//		TerminalNode node31 = factory.createEpsilonNode(5);
//		node30.addChild(node31);
//		node29.addChild(node30);
//		node22.addChild(node23);
//		node22.addChild(node29);
//		node21.addChild(node22);
//		node17.addChild(node18);
//		node17.addChild(node21);
//		node16.addChild(node17);
//		node14.addChild(node15);
//		node14.addChild(node16);
//		node13.addChild(node14);
//		NonterminalNode node32 = factory.createNonterminalNode("E1", 0, 5, 7);
//		PackedNode node33 = factory.createPackedNode("E1 ::= + T E1 .", 7, node32);
//		IntermediateNode node34 = factory.createIntermediateNode("E1 ::= + T . E1", 5, 7);
//		PackedNode node35 = factory.createPackedNode("E1 ::= + T . E1", 6, node34);
//		TerminalNode node36 = factory.createTerminalNode("+", 5, 6);
//		NonterminalNode node37 = factory.createNonterminalNode("T", 0, 6, 7);
//		PackedNode node38 = factory.createPackedNode("T ::= F T1 .", 7, node37);
//		NonterminalNode node39 = factory.createNonterminalNode("F", 0, 6, 7);
//		PackedNode node40 = factory.createPackedNode("F ::= a .", 7, node39);
//		TerminalNode node41 = factory.createTerminalNode("a", 6, 7);
//		node40.addChild(node41);
//		node39.addChild(node40);
//		NonterminalNode node42 = factory.createNonterminalNode("T1", 0, 7, 7);
//		PackedNode node43 = factory.createPackedNode("T1 ::= .", 7, node42);
//		TerminalNode node44 = factory.createEpsilonNode(7);
//		node43.addChild(node44);
//		node42.addChild(node43);
//		node38.addChild(node39);
//		node38.addChild(node42);
//		node37.addChild(node38);
//		node35.addChild(node36);
//		node35.addChild(node37);
//		node34.addChild(node35);
//		NonterminalNode node45 = factory.createNonterminalNode("E1", 0, 7, 7);
//		PackedNode node46 = factory.createPackedNode("E1 ::= .", 7, node45);
//		node46.addChild(node44);
//		node45.addChild(node46);
//		node33.addChild(node34);
//		node33.addChild(node45);
//		node32.addChild(node33);
//		node12.addChild(node13);
//		node12.addChild(node32);
//		node11.addChild(node12);
//		node2.addChild(node3);
//		node2.addChild(node11);
//		node1.addChild(node2);
//		return node1;
//	}

}
