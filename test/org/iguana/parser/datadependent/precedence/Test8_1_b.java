package org.iguana.parser.datadependent.precedence;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.*;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Test8_1_b {

    @Test
    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (^) E  {LEFT,1,LEFT_RIGHT_REC} LEFT(1,1,1) PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(94).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setAssociativityGroup(new AssociativityGroup(Associativity.LEFT,PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{}),1,1,1)).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (-) E  {UNDEFINED,1,RIGHT_REC} LEFT(1,1,1) PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setAssociativityGroup(new AssociativityGroup(Associativity.LEFT,PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{}),1,1,1)).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (+) E  {UNDEFINED,1,RIGHT_REC} LEFT(1,1,1) PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setAssociativityGroup(new AssociativityGroup(Associativity.LEFT,PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{}),1,1,1)).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
// E ::= E (*) E  {UNDEFINED,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,2,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
.build();

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("-a^a");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
    }
}
