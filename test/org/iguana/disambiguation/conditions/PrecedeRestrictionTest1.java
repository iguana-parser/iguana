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
//import org.iguana.grammar.symbol.*;
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
// * S ::= "for" L? Id | "forall"
// *
// * Id ::= [a-z] !<< [a-z]+ !>> [a-z]
// *
// * L ::= " "
// *
// * @author Ali Afroozeh
// *
// */
//public class PrecedeRestrictionTest1 {
//
//	private Grammar grammar;
//
//	private Nonterminal S = Nonterminal.withName("S");
//	private Terminal forr = Terminal.from(Seq.from("for"));
//	private Terminal forall = Terminal.from(Seq.from("forall"));
//	private Nonterminal L = Nonterminal.withName("L");
//	private Nonterminal Id = Nonterminal.withName("Id");
//	private Terminal ws = Terminal.from(Char.from(' '));
//	private Terminal az = Terminal.from(CharRange.in('a', 'z'));
//
//	private Plus AZPlus = Plus.builder(az).addPostCondition(RegularExpressionCondition.notFollow(Char.from(' ')))
//			                              .addPreCondition(RegularExpressionCondition.notPrecede(CharRange.in('a', 'z'))).build();
//
//	@Before
//	public void init() {
//		Rule r1 = Rule.withHead(S).addSymbols(forr, Opt.from(L), Id).build();
//		Rule r2 = Rule.withHead(S).addSymbol(forall).build();
//		Rule r3 = Rule.withHead(Id).addSymbol(AZPlus).build();
//		Rule r4 = Rule.withHead(L).addSymbol(ws).build();
//		grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();
//        grammar = new EBNFToBNF().transform(grammar);
//    }
//
//	@Test
//	public void test() {
//		Input input = Input.fromString("forall");
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//}
