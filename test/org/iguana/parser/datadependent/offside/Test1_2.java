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
//package org.iguana.parser.datadependent.offside;
//
//import iguana.regex.Alt;
//import iguana.regex.Char;
//import iguana.regex.CharRange;
//import iguana.regex.Seq;
//import iguana.utils.input.Input;
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.condition.ConditionType;
//import org.iguana.grammar.condition.RegularExpressionCondition;
//import org.iguana.grammar.symbol.*;
//import org.iguana.grammar.transformation.DesugarAlignAndOffside;
//import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
//import org.iguana.grammar.transformation.EBNFToBNF;
//import org.iguana.grammar.transformation.LayoutWeaver;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parsetree.ParseTreeNode;
//import org.junit.Test;
//
//import java.util.Arrays;
//
//import static iguana.utils.collections.CollectionsUtil.set;
//import static junit.framework.TestCase.assertNotNull;
//import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
//
//@SuppressWarnings("unused")
//public class Test1_2 {
//
//    @Test
//    public void test() {
//         Grammar grammar =
//
//Grammar.builder()
//.setLayout(Nonterminal.builder("Layout").build())
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// Exp ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
//// Exp ::= Exp (+) (a)  {UNDEFINED,1,LEFT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
//// Stat ::= (x) (=) Exp  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("Stat").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(120).build()).build()).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// Layout ::= WhiteSpace*  !>>  (\u0009-\\u000A | \u000C-\\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("Layout").build()).addSymbol(Star.builder(Nonterminal.builder("WhiteSpace").build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharRange.builder(9, 10).build(), CharRange.builder(12, 13).build(), CharRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// WhiteSpace ::= (\u0009-\\u000A | \u000C-\\u000D | \\u001A | \\u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("WhiteSpace").build()).addSymbol(Terminal.from(Alt.builder(CharRange.builder(9, 10).build(), CharRange.builder(12, 13).build(), CharRange.builder(26, 26).build(), CharRange.builder(32, 32).build()).build())).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// S ::= offside Stat+  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Plus.builder(Offside.builder(Nonterminal.builder("Stat").build()).build()).addSeparators(Arrays.asList(Terminal.builder(Seq.builder(Char.builder(59).build()).build()).build())).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//.build();
//         grammar = new EBNFToBNF().transform(grammar);
//
//         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
//
//         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
//         desugarAlignAndOffside.doOffside();
//
//		 grammar = desugarAlignAndOffside.transform(grammar);
//         System.out.println(grammar.toStringWithOrderByPrecedence());
//
//         grammar = new LayoutWeaver().transform(grammar);
//
//         Input input = Input.fromString("x = a;             \n" +
//                                        "        x =        \n" +
//                                        "        a +        \n" +
//                                        "             a");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//    }
//}
