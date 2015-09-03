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
public class iTest2_1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList())).setRightEnds(new HashSet<String>(Arrays.asList())).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// Y ::= (*) E  {UNDEFINED,1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Y").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$Y"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","E","X"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// X ::= (*) Y  {UNDEFINED,1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("X").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("Y").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("").setRightEnd("Y").setLeftEnds(new HashSet<String>(Arrays.asList("$X"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","E","Y"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","X","Y"))).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","X","Y"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E X  {LEFT,2,LEFT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("X").build()).setRecursion(Recursion.LEFT_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("").setRightEnd("X").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","X","Y"))).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList("$E","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","E","X","Y"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         desugarPrecedenceAndAssociativity.setOP2();
         
		 grammar = desugarPrecedenceAndAssociativity.transform(grammar);
         System.out.println(grammar.toString());

         Input input = Input.fromString("a**a**a+a");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseSuccess());

         Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/",
                          result.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
