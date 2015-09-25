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
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

import iguana.utils.input.Input;

@SuppressWarnings("unused")
public class Test10_1_c {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (*) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         Grammar grammar4 =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// F ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("F").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) T  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("T").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= T  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("T").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// T ::= F  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("T").build()).addSymbol(Nonterminal.builder("F").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// T ::= T (*) F  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("T").build()).addSymbol(Nonterminal.builder("T").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("F").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         Grammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test10_1_c"));

         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         
         desugarPrecedenceAndAssociativity.setOP1();
         
		 Grammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
         System.out.println(grammar1.toStringWithOrderByPrecedence());
         
         grammar2 = new OperatorPrecedence(grammar2.getPrecedencePatterns(), grammar2.getExceptPatterns()).transform(grammar2);
         
         System.out.println(grammar2);
         
         desugarPrecedenceAndAssociativity.setOP2();
         
         Grammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
         System.out.println(grammar3.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a+a*a*a*a*a*a");
         GrammarGraph graph1 = grammar1.toGrammarGraph(input, Configuration.DEFAULT);
         GrammarGraph graph2 = grammar2.toGrammarGraph(input, Configuration.DEFAULT);
         GrammarGraph graph3 = grammar3.toGrammarGraph(input, Configuration.DEFAULT);
         GrammarGraph graph4 = grammar4.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/", graph);

         GLLParser parser1 = ParserFactory.getParser();
         GLLParser parser2 = ParserFactory.getParser();
         GLLParser parser3 = ParserFactory.getParser();
         GLLParser parser4 = ParserFactory.getParser();
         
         ParseResult result1 = parser1.parse(input, graph1, Nonterminal.withName("S"));
         ParseResult result2 = parser2.parse(input, graph2, Nonterminal.withName("S"));
         ParseResult result3 = parser3.parse(input, graph3, Nonterminal.withName("S"));
         ParseResult result4 = parser3.parse(input, graph4, Nonterminal.withName("S"));

         Assert.assertTrue(result1.isParseSuccess());
         Assert.assertTrue(result2.isParseSuccess());
         Assert.assertTrue(result3.isParseSuccess());
         Assert.assertTrue(result4.isParseSuccess());

         // Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/",
         //                   result1.asParseSuccess().getRoot(), input);

         Assert.assertEquals(0, result1.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result2.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result3.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result4.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         
         System.out.println("OP scheme 1:");
         System.out.println(result1.asParseSuccess().getStatistics());
         System.out.println("Shape-preserving rewriting:");
         System.out.println(result2.asParseSuccess().getStatistics());
         System.out.println("OP scheme 2:");
         System.out.println(result3.asParseSuccess().getStatistics());
         System.out.println("Standard rewriting:");
         System.out.println(result4.asParseSuccess().getStatistics());
    }
}
