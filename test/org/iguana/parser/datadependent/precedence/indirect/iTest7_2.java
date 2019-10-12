package org.iguana.parser.datadependent.precedence.indirect;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.*;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class iTest7_2 {

    @Test
    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()
.addEBNFr("Arg+",new HashSet<String>(Arrays.asList("$E","$Arg","E","Arg")))
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("").setRightEnd("").setLeftEnds(new HashSet<String>(Arrays.asList())).setRightEnds(new HashSet<String>(Arrays.asList())).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// Arg ::= (b)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("Arg").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(98).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$Arg").setRightEnd("$Arg").setLeftEnds(new HashSet<String>(Arrays.asList("$E","$Arg","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","E","Arg+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// Arg ::= E  {UNDEFINED,1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("Arg").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E","$Arg","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","E","Arg+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("$E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (&) E  {NON_ASSOC,2,RIGHT_REC} NON_ASSOC(2,2,2) PREC(2,2)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(38).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.NON_ASSOC).setPrecedence(2).setAssociativityGroup(new AssociativityGroup(Associativity.NON_ASSOC,PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{}),2,2,2)).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (*) E  {NON_ASSOC,2,RIGHT_REC} NON_ASSOC(2,2,2) PREC(2,2)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("$E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.NON_ASSOC).setPrecedence(2).setAssociativityGroup(new AssociativityGroup(Associativity.NON_ASSOC,PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{}),2,2,2)).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E Arg+  {NON_ASSOC,2,LEFT_REC} NON_ASSOC(2,2,2) PREC(2,2)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Plus.builder(Nonterminal.builder("Arg").build()).build()).setRecursion(Recursion.LEFT_REC).setiRecursion(Recursion.iRIGHT_REC).setLeftEnd("E").setRightEnd("Arg+").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.NON_ASSOC).setPrecedence(2).setAssociativityGroup(new AssociativityGroup(Associativity.NON_ASSOC,PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{}),2,2,2)).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (^) E  {RIGHT,3,LEFT_RIGHT_REC} PREC(3,3)
.addRule(RuntimeRule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","Arg","Arg+"))).setAssociativity(Associativity.RIGHT).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,-1,false,false,true,new Integer[]{2},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setiRecursion(Recursion.NON_REC).setLeftEnd("E").setRightEnd("E").setLeftEnds(new HashSet<String>(Arrays.asList("$E","E"))).setRightEnds(new HashSet<String>(Arrays.asList("$E","$Arg","E","Arg","Arg+"))).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         precedenceAndAssociativity.setOP2();

         grammar = precedenceAndAssociativity.transform(grammar);
         System.out.println(grammar.toString());

         Input input = Input.fromString("abb");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParserTree(input);

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
    }
}
