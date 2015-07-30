package org.iguana.parser.datadependent.precedence;

import java.io.File;
import java.util.Arrays;

import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;

import static org.iguana.grammar.symbol.LayoutStrategy.*;

import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarState;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.*;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Visualization;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

import static org.iguana.util.CollectionsUtil.*;

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
         
         Grammar grammar1 = Grammar.load(new File("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/precedence/Test5PaperToCompare"));
         
         Grammar grammar2 =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (-) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build())
		.addSymbol(Nonterminal.builder("E").apply(AST.var("p")).setLabel("l")
				.addPostCondition(DataDependentCondition.predicate(AST.greaterEq(AST.val("l"), AST.integer(1))))
				.addPreCondition(DataDependentCondition.predicate(AST.lessEq(AST.var("p"), AST.integer(1)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(2)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(1), AST.val("r")))).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build())
		.addSymbol(Return.ret(AST.integer(7))).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (() E ())  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(0)).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build())
		.addSymbol(Return.ret(AST.integer(7))).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build())
		.addSymbol(Nonterminal.builder("E").apply(AST.var("p")).setLabel("l")
				.addPostCondition(DataDependentCondition.predicate(AST.greaterEq(AST.val("l"), AST.integer(2))))
				.addPreCondition(DataDependentCondition.predicate(AST.lessEq(AST.var("p"), AST.integer(2)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(3)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(2), AST.val("r")))).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (+) E  {UNDEFINED,3,RIGHT_REC} PREC(3,3) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(3)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(3), AST.val("r")))).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (/) E  {LEFT,4,LEFT_RIGHT_REC} PREC(4,4) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build())
		.addSymbol(Nonterminal.builder("E").apply(AST.var("p")).setLabel("l")
				.addPostCondition(DataDependentCondition.predicate(AST.greaterEq(AST.val("l"), AST.integer(4))))
				.addPreCondition(DataDependentCondition.predicate(AST.lessEq(AST.var("p"), AST.integer(4)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(5)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(4), AST.val("r")))).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(4).setPrecedenceLevel(PrecedenceLevel.from(4,4,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
// E ::= E (*) E  {LEFT,5,LEFT_RIGHT_REC} PREC(5,5) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build())
		.addSymbol(Nonterminal.builder("E").apply(AST.var("p")).setLabel("l")
				.addPostCondition(DataDependentCondition.predicate(AST.greaterEq(AST.val("l"), AST.integer(5))))
				.addPreCondition(DataDependentCondition.predicate(AST.lessEq(AST.var("p"), AST.integer(5)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(6)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(5), AST.val("r")))).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(5).setPrecedenceLevel(PrecedenceLevel.from(5,5,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
// E ::= (-) E  {UNDEFINED,6,RIGHT_REC} PREC(6,6) 
.addRule(Rule.withHead(Nonterminal.builder("E").addParameters("p").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build())
		.addSymbol(Nonterminal.builder("E").apply(AST.integer(6)).setLabel("r").build())
		.addSymbol(Return.ret(AST.pr3(AST.integer(6), AST.val("r")))).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(6).setPrecedenceLevel(PrecedenceLevel.from(6,6,6,true,false,true,new Integer[]{3},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").apply(AST.integer(0)).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         grammar1 = new OperatorPrecedence(grammar1.getPrecedencePatterns(), grammar1.getExceptPatterns()).transform(grammar1);
         
         System.out.println(grammar.toStringWithOrderByPrecedence());
         System.out.println(grammar1);
         System.out.println(grammar2);

         Input input = Input.fromString("a*+a*a+a--a/a");
         
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
         GrammarGraph graph1 = grammar1.toGrammarGraph(input, Configuration.DEFAULT);
         GrammarGraph graph2 = grammar2.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         GLLParser parser1 = ParserFactory.getParser(Configuration.DEFAULT, input, grammar1);
         GLLParser parser2 = ParserFactory.getParser(Configuration.DEFAULT, input, grammar2);
         
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));
         ParseResult result1 = parser1.parse(input, graph1, Nonterminal.withName("S"));
         ParseResult result2 = parser1.parse(input, graph2, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseSuccess());
         Assert.assertTrue(result1.isParseSuccess());
         Assert.assertTrue(result2.isParseSuccess());

         Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/precedence/", result2.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
         Assert.assertTrue(result1.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
         Assert.assertTrue(result2.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
         
         System.out.println(result.asParseSuccess().getStatistics());
         System.out.println(result1.asParseSuccess().getStatistics());
         System.out.println(result2.asParseSuccess().getStatistics());
    }
}
