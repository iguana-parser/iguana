package org.iguana.parser.datadependent.precedence;

import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;

import java.io.File;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.PrecedenceLevel;
import org.iguana.grammar.symbol.Recursion;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.junit.Assert;
import org.junit.Test;

import iguana.utils.input.Input;

@SuppressWarnings("unused")
public class Test5PaperToCompare {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (-) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (() E ())  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (+) E  {UNDEFINED,3,RIGHT_REC} PREC(3,3) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (/) E  {LEFT,4,LEFT_RIGHT_REC} PREC(4,4) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(4).setPrecedenceLevel(PrecedenceLevel.from(4,4,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
// E ::= E (*) E  {LEFT,5,LEFT_RIGHT_REC} PREC(5,5) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(5).setPrecedenceLevel(PrecedenceLevel.from(5,5,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
// E ::= (-) E  {UNDEFINED,6,RIGHT_REC} PREC(6,6) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(6).setPrecedenceLevel(PrecedenceLevel.from(6,6,6,true,false,true,new Integer[]{3},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         Grammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test5PaperToCompare"));

         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         
         desugarPrecedenceAndAssociativity.setOP1();
         
		 Grammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
         grammar2 = new OperatorPrecedence(grammar2.getPrecedencePatterns(), grammar2.getExceptPatterns()).transform(grammar2);
         
         desugarPrecedenceAndAssociativity.setOP2();
         
         Grammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
         
         System.out.println(grammar1.toStringWithOrderByPrecedence());
         System.out.println(grammar2);
         System.out.println(grammar3.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a*+a*a+a--a/a");

         GrammarGraph graph1 = GrammarGraph.from(grammar1, input, Configuration.builder().setEnvironmentImpl(EnvironmentImpl.TRIE).build());
         GrammarGraph graph2 = GrammarGraph.from(grammar2, input, Configuration.DEFAULT);
         GrammarGraph graph3 = GrammarGraph.from(grammar3, input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/", graph);

         ParseResult result1 = Iguana.parse(input, graph1, Nonterminal.withName("S"));
         ParseResult result2 = Iguana.parse(input, graph2, Nonterminal.withName("S"));
         ParseResult result3 = Iguana.parse(input, graph3, Nonterminal.withName("S"));

         Assert.assertTrue(result1.isParseSuccess());
         Assert.assertTrue(result2.isParseSuccess());
         Assert.assertTrue(result3.isParseSuccess());

         // Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/", result3.asParseSuccess().getSPPFNode(), input);

         Assert.assertEquals(0, result1.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result2.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result3.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         
         System.out.println("OP scheme 1:");
         System.out.println(result1.asParseSuccess().getStatistics());
         System.out.println("Shape-preserving rewriting:");
         System.out.println(result2.asParseSuccess().getStatistics());
         System.out.println("OP scheme 2:");
         System.out.println(result3.asParseSuccess().getStatistics());
    }
}
