package org.iguana.parser.datadependent.precedence.indirect;

import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.iguana.util.CollectionsUtil.list;

import java.util.Arrays;
import java.util.HashSet;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.Character;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

import iguana.utils.input.Input;

@SuppressWarnings("unused")
public class iTest5_1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()
.addEBNFr("{E, (*), null}+",new HashSet<String>(Arrays.asList("$E","E")))
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList())).setRightEnds(new HashSet<String>(Arrays.asList())).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (-) E  {UNDEFINED,1,RIGHT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","{E, (*), null}+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("$E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","{E, (*), null}+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (- >) E  {RIGHT,2,LEFT_RIGHT_REC} PREC(2,2) arrow
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","{E, (*), null}+"))).setAssociativity(Associativity.RIGHT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,true,new Integer[]{1},false,new Integer[]{})).setLabel("arrow").build())
// E ::= E (*) E+  {NON_ASSOC,3,LEFT_REC} PREC(3,3) star
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("E").addExcept("star") .addExcept("arrow").build()).addSeparators(list(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build())).build()).setRecursion(Recursion.LEFT_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("E").setRightEnd("{E, (*), null}+").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","{E, (*), null}+"))).setAssociativity(Associativity.NON_ASSOC).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,-1,false,true,true,new Integer[]{1},false,new Integer[]{})).setLabel("star").build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","E","{E, (*), null}+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         precedenceAndAssociativity.setOP2();
         
		 grammar = precedenceAndAssociativity.transform(grammar);
         System.out.println(grammar.toString());

         Input input = Input.fromString("a*a*a");
         GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/indirect/", graph);

         ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseSuccess());

         Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
    }
}
