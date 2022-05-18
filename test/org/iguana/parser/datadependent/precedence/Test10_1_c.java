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
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Test10_1_c {

    @Test
    public void test() throws FileNotFoundException {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (*) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         RuntimeGrammar grammar4 =

RuntimeGrammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// F ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("F").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (+) T  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("T").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= T  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("T").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// T ::= F  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("T").build()).addSymbol(new Nonterminal.Builder("F").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// T ::= T (*) F  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("T").build()).addSymbol(new Nonterminal.Builder("T").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("F").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();
         
         RuntimeGrammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test10_1_c")).toRuntimeGrammar();

         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
         
         desugarPrecedenceAndAssociativity.setOP1();
         
		 RuntimeGrammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);

         desugarPrecedenceAndAssociativity.setOP2();
         
         RuntimeGrammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);

         Input input = Input.fromString("a+a*a*a*a*a*a");

        IguanaParser parser1 = new IguanaParser(grammar1);
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
