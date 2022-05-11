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
//import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
//import static org.iguana.util.CollectionsUtil.set;
//
//import java.util.Arrays;
//
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.GrammarGraph;
//import org.iguana.grammar.condition.ConditionType;
//import org.iguana.grammar.condition.RegularExpressionCondition;
//import org.iguana.grammar.symbol.Align;
//import org.iguana.grammar.symbol.Associativity;
//import org.iguana.regex.Character;
//import org.iguana.regex.CharacterRange;
//import org.iguana.grammar.symbol.Nonterminal;
//import org.iguana.grammar.symbol.Offside;
//import org.iguana.grammar.symbol.PrecedenceLevel;
//import org.iguana.grammar.symbol.Recursion;
//import org.iguana.grammar.symbol.Rule;
//import org.iguana.grammar.symbol.Start;
//import org.iguana.grammar.symbol.Terminal;
//import org.iguana.grammar.transformation.DesugarAlignAndOffside;
//import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
//import org.iguana.grammar.transformation.EBNFToBNF;
//import org.iguana.grammar.transformation.LayoutWeaver;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parser.ParseResult;
//import org.iguana.regex.Alt;
//import org.iguana.regex.Plus;
//import org.iguana.regex.Sequence;
//import org.iguana.regex.Star;
//import org.iguana.util.Configuration;
//import org.junit.Assert;
//import org.junit.Test;
//
//import iguana.utils.input.Input;
//
//@SuppressWarnings("unused")
//public class Test2_1 {
//
//    @Test
//    public void test() {
//         Grammar grammar =
//
//Grammar.builder()
//.setLayout(new Nonterminal.Builder("Layout").build())
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// Exp ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("Exp").build()).addSymbol(new Terminal.Builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
//// Exp ::= Exp (+) (a)  {UNDEFINED,1,LEFT_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("Exp").build()).addSymbol(new Nonterminal.Builder("Exp").build()).addSymbol(new Terminal.Builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(new Terminal.Builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
//// Stat ::= (x) (=) Exp  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("Stat").build()).addSymbol(new Terminal.Builder(Sequence.builder(Character.builder(120).build()).build()).build()).addSymbol(new Terminal.Builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(new Nonterminal.Builder("Exp").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// Layout ::= WhiteSpace*  !>>  (\u0009-\\u000A | \u000C-\\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("Layout").build()).addSymbol(Star.builder(new Nonterminal.Builder("WhiteSpace").build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// WhiteSpace ::= (\u0009-\\u000A | \u000C-\\u000D | \\u001A | \\u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("WhiteSpace").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(26, 26).build(), CharacterRange.builder(32, 32).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//// S ::= align offside Stat+  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(new Nonterminal.Builder("S").build()).addSymbol(Align.builder(new Plus.Builder(Offside.builder(new Nonterminal.Builder("Stat").build()).build()).addSeparators(Arrays.asList(new Terminal.Builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
//.build();
//
//         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
//         desugarAlignAndOffside.doAlign();
//
//         grammar = desugarAlignAndOffside.transform(grammar);
//         // System.out.println(grammar.toStringWithOrderByPrecedence());
//
//         grammar = new EBNFToBNF().transform(grammar);
//         // System.out.println(grammar);
//
//         desugarAlignAndOffside.doOffside();
//         grammar = desugarAlignAndOffside.transform(grammar);
//         // System.out.println(grammar.toStringWithOrderByPrecedence());
//
//         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
//         System.out.println(grammar.toStringWithOrderByPrecedence());
//
//         grammar = new LayoutWeaver().transform(grammar);
//
//         Input input = Input.fromString(  "  x = a;             \n"
//                                        + "  x =                \n"
//                                        + "       a +           \n"
//                                        + "     a;              \n"
//                                        + "  x = a + a;         \n"
//                                        + "  x =                \n"
//                                        + "         a           \n"
//                                        + "        +            \n"
//                                        + "       a             \n"
//                                        + "      +              \n"
//                                        + "     a               \n");
//
//         Start startSymbol = grammar.getStartSymbol(Nonterminal.withName("S"));
//
//         GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
//
//         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/", graph);
//
//         ParseResult result = IguanaParser.getParserTree(input, graph, startSymbol);
//
//         Assert.assertTrue(result.isParseSuccess());
//
//         // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/",
//         //                   result.asParseSuccess().getResult(), input);
//
//         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
//    }
//}
