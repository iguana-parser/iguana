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
//import iguana.regex.Alt;
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
//import static org.junit.Assert.assertNull;
//
//
///**
// *
// * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
// *
// * @author Ali Afroozeh
// *
// */
//public class KeywordExclusionTest2 {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//
//		Nonterminal Id = Nonterminal.withName("Id");
//		CharRange az = CharRange.in('a', 'z');
//
//		Seq<Char> iff = Seq.from("if");
//		Seq<Char> when = Seq.from("when");
//		Seq<Char> doo = Seq.from("do");
//		Seq<Char> whilee = Seq.from("while");
//		Alt<?> alt = Alt.from(iff, when, doo, whilee);
//		Plus AZPlus = Plus.builder(Terminal.from(az)).addPostCondition(RegularExpressionCondition.notFollow(az))
//									  .addPostCondition(RegularExpressionCondition.notMatch(alt)).build();
//
//		Rule r1 = Rule.withHead(Id).addSymbol(AZPlus).build();
//
//		grammar = Grammar.builder().addRule(r1).build();
//        grammar = new EBNFToBNF().transform(grammar);
//    }
//
//	@Test
//	public void testWhen() {
//		Input input = Input.fromString("when");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNull(result);
//	}
//
//	@Test
//	public void testIf() {
//		Input input = Input.fromString("if");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNull(result);
//	}
//
//	@Test
//	public void testDo() {
//		Input input = Input.fromString("do");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNull(result);
//	}
//
//	@Test
//	public void testWhile() {
//		Input input = Input.fromString("while");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNull(result);
//	}
//
//}
