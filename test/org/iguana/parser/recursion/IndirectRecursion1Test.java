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
//import static junit.framework.TestCase.assertNotNull;
//
///**
// *
// * A ::= B C | a
// *
// * B ::= A | b
// *
// * C ::= c
// *
// *
// * @author Ali Afroozeh
// *
// */
//public class IndirectRecursion1Test {
//
//	private Grammar grammar;
//
//	private Nonterminal A = Nonterminal.withName("A");
//	private Nonterminal B = Nonterminal.withName("B");
//	private Nonterminal C = Nonterminal.withName("C");
//	private Terminal a = Terminal.from(Char.from('a'));
//	private Terminal b = Terminal.from(Char.from('b'));
//	private Terminal c = Terminal.from(Char.from('c'));
//
//	@Before
//	public void createGrammar() {
//		Rule r1 = Rule.withHead(A).addSymbols(B, C).build();
//		Rule r2 = Rule.withHead(A).addSymbols(a).build();
//		Rule r3 = Rule.withHead(B).addSymbols(A).build();
//		Rule r4 = Rule.withHead(B).addSymbols(b).build();
//		Rule r5 = Rule.withHead(C).addSymbols(c).build();
//
//		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3)
//								  	   .addRule(r4).addRule(r5).build();
//	}
//
//
////	@Test
////	public void testFirstFollowSets() {
////		assertEquals(set(a, b), grammar.getFirstSet(A));
////		assertEquals(set(a, b), grammar.getFirstSet(B));
////		assertEquals(set(c), grammar.getFirstSet(C));
////		assertEquals(set(c, EOF.getInstance()), grammar.getFollowSet(A));
////		assertEquals(set(c, EOF.getInstance()), grammar.getFollowSet(B));
////	}
//
//	@Test
//	public void testParser() {
//		Input input = Input.fromString("bc");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
////	private SPPFNode expectedSPPF(GrammarGraph registry) {
////		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
////		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 2);
////		PackedNode node2 = factory.createPackedNode("A ::= B C .", 1, node1);
////		NonterminalNode node3 = factory.createNonterminalNode("B", 0, 0, 1);
////		PackedNode node4 = factory.createPackedNode("B ::= b .", 1, node3);
////		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
////		node4.addChild(node5);
////		node3.addChild(node4);
////		NonterminalNode node6 = factory.createNonterminalNode("C", 0, 1, 2);
////		PackedNode node7 = factory.createPackedNode("C ::= c .", 2, node6);
////		TerminalNode node8 = factory.createTerminalNode("c", 1, 2);
////		node7.addChild(node8);
////		node6.addChild(node7);
////		node2.addChild(node3);
////		node2.addChild(node6);
////		node1.addChild(node2);
////		return node1;
////	}
//
//}
