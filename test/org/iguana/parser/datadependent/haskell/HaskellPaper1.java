package org.jgll.parser.datadependent.haskell;

import java.util.Arrays;
import org.jgll.datadependent.ast.AST;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import static org.jgll.grammar.symbol.LayoutStrategy.*;
import org.jgll.grammar.transformation.DesugarAlignAndOffside;
import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.*;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * After align

Decls ::= ignore({ Decl (; Decl)* })
        | a0:(offside [indent(a1.lExt) == indent(a0.lExt)] a1:Decl)*

Decl ::= FunLHS RHS

RHS ::= '=' Exp 'where' Decls

 * After offside and ignore
 
({ Decl (; Decl)* })(i,ind,fst) ::= o0:{ [f(i,ind,fst,o0.lExt)] o1:Decl(g(i,fst,o1.lExt,1),ind,g(i,fst,o1.lExt,0)) o2:Star#1(g(i,fst,o2.lExt,1),ind,g(i,fst,o2.lExt,0)) o3:} [f(i,ind,fst,o3.lExt)]

Decl(i,ind,fst) ::= o0:FunLHS(g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0)) o1:RHS(g(i,fst,o1.lExt,1),ind,g(i,fst,o1.lExt,0))

Decls(i,ind,fst) ::= ({ Decl (; Decl)* })(0,0,0)
                   | a0:Star#2(a0.lExt,g(i,fst,a0.lExt,1),ind,g(i,fst,a0.lExt,0))

RHS(i,ind,fst) ::= o0:= [f(i,ind,fst,o0.lExt)] o1:Exp(g(i,fst,o1.lExt,1),ind,g(i,fst,o1.lExt,0)) o2:where [f(i,ind,fst,o2.lExt)] o3:Decls(g(i,fst,o3.lExt,1),ind,g(i,fst,o3.lExt,0))

Plus#2(a0.lExt,i,ind,fst) ::= [f(i,ind,fst,a1.lExt)],[indent(a1.lExt) == indent(a0.lExt)] a1:Decl(a1.lExt,indent(a1.lExt),1)
                            | Plus#2(a0.lExt,g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0)) [f(i,ind,fst,a1.lExt)],[indent(a1.lExt) == indent(a0.lExt)] a1:Decl(a1.lExt,indent(a1.lExt),1)

Star#2(a0.lExt,i,ind,fst) ::= Plus#2(a0.lExt,g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0))
                            | epsilon

Star#2(i,ind,fst) ::= Plus#2(g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0))
                    | epsilon

Seq#1(i,ind,fst) ::= o0:';' [f(i,ind,fst,o0.lExt)] o1:Decl(g(i,fst,o1.lExt,1),ind,g(i,fst,o1.lExt,0))
Plus#1(i,ind,fst) ::= o0:Seq#1(g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0))
                    | o0:Plus#1(g(i,fst,o0.lExt,1),ind,g(i,fst,o0.lExt,0)) o1:Seq#1(g(i,fst,o1.lExt,1),ind,g(i,fst,o1.lExt,0))

 */

@SuppressWarnings("unused")
public class HaskellPaper1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()
.setLayout(Nonterminal.builder("L").build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// RHS ::= (=) Exp (w h e r e) Decls  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("RHS").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// FunLHS ::= (A-Z | a-z)+  !>>  (A-Z | a-z)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Plus.builder(Alt.builder(CharacterRange.builder(65, 90).build(), CharacterRange.builder(97, 122).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(65, 90).build(), CharacterRange.builder(97, 122).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Module ::= Decls  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Module").build()).addSymbol(Nonterminal.builder("Decls").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// L ::= (\u0009-\\u000A | \\u000D | \u0020)*  !>>  (\u0009-\\u000A | \\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("L").build()).addSymbol(Star.builder(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Exp ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Decl ::= FunLHS RHS  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Decl").build()).addSymbol(Nonterminal.builder("FunLHS").build()).addSymbol(Nonterminal.builder("RHS").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Decls ::= ignore (({) Decl ((;) Decl)* (}))  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Decls").build()).addSymbol(Ignore.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build(), Nonterminal.builder("Decl").build(), Star.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build(), Nonterminal.builder("Decl").build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Decls ::= align offside Decl*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Decls").build()).addSymbol(Align.builder(Star.builder(Offside.builder(Nonterminal.builder("Decl").build()).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
         desugarAlignAndOffside.doAlign();

         grammar = desugarAlignAndOffside.transform(grammar);
         System.out.println("DO ALIGN...");
         System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         desugarAlignAndOffside.doOffside();
         grammar = desugarAlignAndOffside.transform(grammar);
         System.out.println("DO OFFSIDE...");
         System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new LayoutWeaver().transform(grammar);

//         Input input = Input.fromString("");
//         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
//
//         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/haskell/", graph);
//
//         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
//         ParseResult result = parser.parse(input, graph, Nonterminal.withName("Module"));
//
//         Assert.assertTrue(result.isParseSuccess());
//
//         // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/haskell/",
//         //                   result.asParseSuccess().getRoot(), input);
//
//         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
