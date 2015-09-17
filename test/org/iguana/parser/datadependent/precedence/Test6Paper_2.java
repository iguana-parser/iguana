package org.iguana.parser.datadependent.precedence;

import java.util.Arrays;
import java.util.HashSet;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.RegularExpressionCondition;
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
public class Test6Paper_2 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()
.setLayout(Nonterminal.builder("L").build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList())).setRightEnds(new HashSet<String>(Arrays.asList())).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// L ::= (\u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("L").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(32).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$L").setRightEnd("$L").setLeftEnds(new HashSet<String>(Arrays.asList())).setRightEnds(new HashSet<String>(Arrays.asList())).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// Id ::= (i d)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Id").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(100).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$Id").setRightEnd("$Id").setLeftEnds(new HashSet<String>(Arrays.asList("$Id"))).setRightEnds(new HashSet<String>(Arrays.asList("$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (() E ())  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("$E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (i f) E (t h e n) E  {UNDEFINED,1,RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("$E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {UNDEFINED,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,2,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
// E ::= E (.) Id  {UNDEFINED,3,LEFT_REC} PREC(3,3) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Id").build()).setRecursion(Recursion.LEFT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("Id").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,false,true,true,new Integer[]{1},false,new Integer[]{})).build())
// E ::= E (. [) E (])  {UNDEFINED,3,LEFT_REC} PREC(3,3) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("$E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,false,true,true,new Integer[]{1},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","E","Id","$Id"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         precedenceAndAssociativity.setOP2();
         
		 grammar = precedenceAndAssociativity.transform(grammar);
         System.out.println(grammar.toString());

         // grammar = new LayoutWeaver().transform(grammar);

         Input input = Input.fromString("a+ifathena+a");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseSuccess());

         Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/",
                           result.asParseSuccess().getRoot(), input);

         Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
    }
}
