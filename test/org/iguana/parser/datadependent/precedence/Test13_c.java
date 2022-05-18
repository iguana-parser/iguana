package org.iguana.parser.datadependent.precedence;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.parser.IguanaParser;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Test13_c {

    @Test
    public void test() throws FileNotFoundException {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (-) E  {UNDEFINED,1,RIGHT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
// E ::= E (*) E  {LEFT,3,LEFT_RIGHT_REC} PREC(3,3) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,-1,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         RuntimeGrammar grammar4 =

RuntimeGrammar.builder()

// E5 ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E5").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E3 ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E3").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E3 ::= E3 (*) E5  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E3").build()).addSymbol(new Nonterminal.Builder("E3").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E5").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E4 ::= E3  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E4").build()).addSymbol(new Nonterminal.Builder("E3").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E4 ::= E4 (+) E3  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E4").build()).addSymbol(new Nonterminal.Builder("E4").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E3").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E1 ::= E3 (*) E2  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E1").build()).addSymbol(new Nonterminal.Builder("E3").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E2").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E1 ::= E2  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E1").build()).addSymbol(new Nonterminal.Builder("E2").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E2 ::= (-) E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E2").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E2 ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E2").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E4 (+) E1  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E4").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E1").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E1  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E1").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         RuntimeGrammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test13_c")).toRuntimeGrammar();

         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();

         desugarPrecedenceAndAssociativity.setOP1();

		 RuntimeGrammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
         System.out.println(grammar1.toStringWithOrderByPrecedence());

         desugarPrecedenceAndAssociativity.setOP2();

         RuntimeGrammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
         System.out.println(grammar3.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a+-a+a");

        IguanaParser parser1 = new IguanaParser(grammar1, Configuration.builder().setEnvironmentImpl(EnvironmentImpl.TRIE).build());
        assertNotNull(parser1.getParseTree());
        assertEquals(0, parser1.getStatistics().getAmbiguousNodesCount());

        IguanaParser parser2 = new IguanaParser(grammar2);
        assertNotNull(parser2.getParseTree());
        assertEquals(0, parser2.getStatistics().getAmbiguousNodesCount());

        IguanaParser parser3 = new IguanaParser(grammar3);
        assertNotNull(parser3.getParseTree());
        assertEquals(0, parser3.getStatistics().getAmbiguousNodesCount());

        IguanaParser parser4 = new IguanaParser(grammar4);
        assertNotNull(parser1.getParseTree());
        assertEquals(0, parser4.getStatistics().getAmbiguousNodesCount());
    }
}
