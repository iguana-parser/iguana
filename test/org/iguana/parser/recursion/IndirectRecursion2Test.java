///*
// * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * 1. Redistributions of source code must retain the above copyright notice, this
// *    list of conditions and the following disclaimer.
// *
// * 2. Redistributions in binary form must reproduce the above copyright notice, this
// *    list of conditions and the following disclaimer in the documentation and/or
// *    other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
// * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
// * OF SUCH DAMAGE.
// *
// */
//
//package org.iguana.parser.recursion;
//
//import iguana.regex.Char;
//import iguana.utils.input.Input;
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.symbol.Nonterminal;
//import org.iguana.grammar.symbol.Rule;
//import org.iguana.grammar.symbol.Terminal;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parsetree.ParseTreeNode;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertNotNull;
//
///**
// *
// * A ::= B A d | a
// *
// * B ::= epsilon | b
// *
// *
// * @author Ali Afroozeh
// *
// */
//public class IndirectRecursion2Test {
//
//	private Grammar grammar;
//
//	private Nonterminal A = Nonterminal.withName("A");
//	private Nonterminal B = Nonterminal.withName("B");
//	private Terminal a = Terminal.from(Char.from('a'));
//	private Terminal b = Terminal.from(Char.from('b'));
//	private Terminal d = Terminal.from(Char.from('d'));
//
//	@Before
//	public void init() {
//		Rule r1 = Rule.withHead(A).addSymbols(B, A, d).build();
//		Rule r2 = Rule.withHead(A).addSymbols(a).build();
//		Rule r3 = Rule.withHead(B).build();
//		Rule r4 = Rule.withHead(B).addSymbols(b).build();
//
//		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
//	}
//
////	@Test
////	public void testNullable() {
////		assertFalse(grammar.isNullable(A));
////		assertTrue(grammar.isNullable(B));
////	}
//
//	@Test
//	public void testParser() {
//		Input input = Input.fromString("ad");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//	//TODO: fix it
//	public void testReachabilityGraph() {
////		Set<HeadGrammarSlot> set = builder.getDirectReachableNonterminals("A");
////		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("A")));
////		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("B")));
//	}
//
////	private SPPFNode expectedSPPF(GrammarGraph registry) {
////		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
////		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 2);
////		PackedNode node2 = factory.createPackedNode("A ::= B A d .", 1, node1);
////		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . d", 0, 1);
////		PackedNode node4 = factory.createPackedNode("A ::= B A . d", 0, node3);
////		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 0);
////		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
////		TerminalNode node7 = factory.createEpsilonNode(0);
////		node6.addChild(node7);
////		node5.addChild(node6);
////		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 1);
////		PackedNode node9 = factory.createPackedNode("A ::= a .", 1, node8);
////		TerminalNode node10 = factory.createTerminalNode("a", 0, 1);
////		node9.addChild(node10);
////		node8.addChild(node9);
////		node4.addChild(node5);
////		node4.addChild(node8);
////		node3.addChild(node4);
////		TerminalNode node11 = factory.createTerminalNode("d", 1, 2);
////		node2.addChild(node3);
////		node2.addChild(node11);
////		node1.addChild(node2);
////		return node1;
////	}
//
//}
