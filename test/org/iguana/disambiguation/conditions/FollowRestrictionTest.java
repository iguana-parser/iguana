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
//package org.iguana.disambiguation.conditions;
//
//import iguana.regex.Char;
//import iguana.regex.CharRange;
//import iguana.regex.Seq;
//import iguana.utils.input.Input;
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.condition.RegularExpressionCondition;
//import org.iguana.grammar.symbol.Nonterminal;
//import org.iguana.grammar.symbol.Plus;
//import org.iguana.grammar.symbol.Rule;
//import org.iguana.grammar.symbol.Terminal;
//import org.iguana.grammar.transformation.EBNFToBNF;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parsetree.ParseTreeNode;
//import org.junit.Before;
//import org.junit.Test;
//
//import static junit.framework.TestCase.assertNotNull;
//
///**
// *
// * S ::= Label !>> ":"
// *
// * Label ::= [a-z]+ !>> [a-z]
// *
// *
// * @author Ali Afroozeh
// *
// */
//public class FollowRestrictionTest {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//		Nonterminal S = Nonterminal.withName("S");
//		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Seq.from(":"))).build();
//		CharRange az = CharRange.in('a', 'z');
//		Plus AZPlus = Plus.builder(Terminal.from(az)).addPreCondition(RegularExpressionCondition.notFollow(az)).build();
//
//		Grammar.Builder builder = new Grammar.Builder();
//
//		Rule r1 = Rule.withHead(S).addSymbol(Label).build();
//		Rule r2 = Rule.withHead(Label).addSymbol(AZPlus).build();
//		builder.addRule(r1).addRule(r2);
//
//		grammar = builder.build();
//        grammar = new EBNFToBNF().transform(grammar);
//	}
//
//	@Test
//	public void testParser() {
//		Input input = Input.fromString("abc:");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//
//    /**
//     *
//     * S ::= a S b
//     *     | a S
//     *     | s
//     *
//     * @author Ali Afroozeh
//     *
//     */
//    public static class DanglingElseGrammar4 {
//
//        Nonterminal S = Nonterminal.withName("S");
//        Terminal s = Terminal.from(Char.from('s'));
//        Terminal a = Terminal.from(Char.from('a'));
//        Terminal b = Terminal.from(Char.from('b'));
//        private Grammar grammar;
//
//        @Before
//        public void init() {
//
//            Grammar.Builder builder = new Grammar.Builder();
//
//            Rule rule1 = Rule.withHead(S).addSymbols(a, S, b).build();
//            builder.addRule(rule1);
//
//            Rule rule2 = Rule.withHead(S).addSymbols(a, S).build();
//            builder.addRule(rule2);
//
//            Rule rule3 = Rule.withHead(S).addSymbols(s).build();
//            builder.addRule(rule3);
//
//            grammar = builder.build();
//        }
//
//        @Test
//        public void test() {
//            Input input = Input.fromString("aasb");
//            IguanaParser parser = new IguanaParser(grammar);
//            ParseTreeNode result = parser.getParserTree(input);
//
//            assertNotNull(result);
//    //		assertTrue(result.asParseSuccess().getResult().deepEquals(getExpectedSPPF(parser.getGrammarGraph())));
//        }
//
//    //	private SPPFNode getExpectedSPPF(GrammarGraph registry) {
//    //		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
//    //		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 4);
//    //		PackedNode node2 = factory.createPackedNode("S ::= a S b .", 3, node1);
//    //		IntermediateNode node3 = factory.createIntermediateNode("S ::= a S . b", 0, 3);
//    //		PackedNode node4 = factory.createPackedNode("S ::= a S . b", 1, node3);
//    //		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
//    //		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 3);
//    //		PackedNode node7 = factory.createPackedNode("S ::= a S .", 2, node6);
//    //		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
//    //		NonterminalNode node9 = factory.createNonterminalNode("S", 0, 2, 3);
//    //		PackedNode node10 = factory.createPackedNode("S ::= s .", 3, node9);
//    //		TerminalNode node11 = factory.createTerminalNode("s", 2, 3);
//    //		node10.addChild(node11);
//    //		node9.addChild(node10);
//    //		node7.addChild(node8);
//    //		node7.addChild(node9);
//    //		node6.addChild(node7);
//    //		node4.addChild(node5);
//    //		node4.addChild(node6);
//    //		node3.addChild(node4);
//    //		TerminalNode node12 = factory.createTerminalNode("b", 3, 4);
//    //		node2.addChild(node3);
//    //		node2.addChild(node12);
//    //		PackedNode node13 = factory.createPackedNode("S ::= a S .", 1, node1);
//    //		NonterminalNode node15 = factory.createNonterminalNode("S", 0, 1, 4);
//    //		PackedNode node16 = factory.createPackedNode("S ::= a S b .", 3, node15);
//    //		IntermediateNode node17 = factory.createIntermediateNode("S ::= a S . b", 1, 3);
//    //		PackedNode node18 = factory.createPackedNode("S ::= a S . b", 2, node17);
//    //		node18.addChild(node8);
//    //		node18.addChild(node9);
//    //		node17.addChild(node18);
//    //		node16.addChild(node17);
//    //		node16.addChild(node12);
//    //		node15.addChild(node16);
//    //		node13.addChild(node5);
//    //		node13.addChild(node15);
//    //		node1.addChild(node2);
//    //		node1.addChild(node13);
//    //		return node1;
//    //	}
//
//    }
//}
