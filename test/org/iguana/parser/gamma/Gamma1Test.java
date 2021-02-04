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

package org.iguana.parser.gamma;


import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * 
 * S ::= A S d | B S | epsilon
 * A ::= a | c
 * B ::= a | b
 * 
 */
public class Gamma1Test {
	
	private Terminal a = Terminal.from(Char.from('a'));
	private Terminal b = Terminal.from(Char.from('b'));
	private Terminal c = Terminal.from(Char.from('c'));
	private Terminal d = Terminal.from(Char.from('d'));
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Rule r1 = Rule.withHead(S).addSymbols(A, S, d).build();
		Rule r2 = Rule.withHead(S).addSymbols(B, S).build();
		Rule r3 = Rule.withHead(S).build();
		Rule r4 = Rule.withHead(A).addSymbols(a).build();
		Rule r5 = Rule.withHead(A).addSymbols(c).build();
		Rule r6 = Rule.withHead(B).addSymbols(a).build();
		Rule r7 = Rule.withHead(B).addSymbols(b).build();
		
		grammar = Grammar.builder().addRules(r1, r2, r3, r4, r5, r6, r7).build();
	}
	
//	@Test
//	public void testNullables() {
//		assertTrue(grammar.isNullable(S));
//		assertFalse(grammar.isNullable(A));
//		assertFalse(grammar.isNullable(B));
//	}
//	
//	@Test
//	public void testFirstSets() {
//		assertEquals(set(a, b, c, Epsilon.getInstance()), grammar.getFirstSet(S));
//		assertEquals(set(a, c), grammar.getFirstSet(A));
//		assertEquals(set(a, b), grammar.getFirstSet(B));
//	}
//
//	@Test
//	public void testFollowSets() {
//		assertEquals(set(a, b, c, d, EOF.getInstance()), grammar.getFollowSet(A));
//		assertEquals(set(d, EOF.getInstance()), grammar.getFollowSet(S));
//	}
	
//	@Test
//	public void testSPPF() {
//		Input input = Input.fromString("aad");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);

//        assertNotNull(result);
//		assertTrue(result.asParseSuccess().getResult().deepEquals(getSPPF(parser.getGrammarGraph())));
//	}
	
//	public SPPFNode getSPPF(GrammarGraph graph) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
//		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3);
//		PackedNode node2 = factory.createPackedNode("S ::= A S d .", 2, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("S ::= A S . d", 0, 2);
//		PackedNode node4 = factory.createPackedNode("S ::= A S . d", 1, node3);
//		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 1);
//		PackedNode node6 = factory.createPackedNode("A ::= a .", 1, node5);
//		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
//		node6.addChild(node7);
//		node5.addChild(node6);
//		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2);
//		PackedNode node9 = factory.createPackedNode("S ::= B S .", 2, node8);
//		NonterminalNode node10 = factory.createNonterminalNode("B", 0, 1, 2);
//		PackedNode node11 = factory.createPackedNode("B ::= a .", 2, node10);
//		TerminalNode node12 = factory.createTerminalNode("a", 1, 2);
//		node11.addChild(node12);
//		node10.addChild(node11);
//		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 2);
//		PackedNode node14 = factory.createPackedNode("S ::= .", 2, node13);
//		TerminalNode node15 = factory.createEpsilonNode(2);
//		node14.addChild(node15);
//		node13.addChild(node14);
//		node9.addChild(node10);
//		node9.addChild(node13);
//		node8.addChild(node9);
//		node4.addChild(node5);
//		node4.addChild(node8);
//		node3.addChild(node4);
//		TerminalNode node16 = factory.createTerminalNode("d", 2, 3);
//		node2.addChild(node3);
//		node2.addChild(node16);
//		PackedNode node17 = factory.createPackedNode("S ::= B S .", 1, node1);
//		NonterminalNode node18 = factory.createNonterminalNode("B", 0, 0, 1);
//		PackedNode node19 = factory.createPackedNode("B ::= a .", 1, node18);
//		node19.addChild(node7);
//		node18.addChild(node19);
//		NonterminalNode node21 = factory.createNonterminalNode("S", 0, 1, 3);
//		PackedNode node22 = factory.createPackedNode("S ::= A S d .", 2, node21);
//		IntermediateNode node23 = factory.createIntermediateNode("S ::= A S . d", 1, 2);
//		PackedNode node24 = factory.createPackedNode("S ::= A S . d", 2, node23);
//		NonterminalNode node25 = factory.createNonterminalNode("A", 0, 1, 2);
//		PackedNode node26 = factory.createPackedNode("A ::= a .", 2, node25);
//		node26.addChild(node12);
//		node25.addChild(node26);
//		node24.addChild(node25);
//		node24.addChild(node13);
//		node23.addChild(node24);
//		node22.addChild(node23);
//		node22.addChild(node16);
//		node21.addChild(node22);
//		node17.addChild(node18);
//		node17.addChild(node21);
//		node1.addChild(node2);
//		node1.addChild(node17);
//		return node1;
//	}
	
}
