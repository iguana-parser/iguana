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
// * S ::= Label !>> "8" [0-9]
// *
// * Label ::= [a-z]+ !>> [a-z]
// *
// *
// * @author Ali Afroozeh
// *
// */
//public class FollowRestrictionTest2 {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//
//		Nonterminal S = Nonterminal.withName("S");
//		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Seq.from("8"))).build();
//		CharRange az = CharRange.in('a', 'z');
//		CharRange zero_nine = CharRange.in('0', '9');
//		Plus AZPlus = Plus.builder(Terminal.from(az)).addPreCondition(RegularExpressionCondition.notFollow(az)).build();
//
//		Rule r1 = Rule.withHead(S).addSymbols(Label, Terminal.from(zero_nine)).build();
//		Rule r2 = Rule.withHead(Label).addSymbol(AZPlus).build();
//
//		grammar = Grammar.builder().addRule(r1).addRule(r2).build();
//        grammar = new EBNFToBNF().transform(grammar);
//    }
//
//	@Test
//	public void testParser1() {
//		Input input = Input.fromString("abc8");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//	@Test
//	public void testParser2() {
//		Input input = Input.fromString("abc3");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//}
