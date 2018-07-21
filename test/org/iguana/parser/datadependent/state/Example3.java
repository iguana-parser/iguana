package org.iguana.parser.datadependent.state;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarState;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Example3 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// A ::= B C  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("A").build()).addSymbol(Nonterminal.builder("B").build()).addSymbol(Nonterminal.builder("C").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// G ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("G").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// F ::= (a) do x = 666; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("F").build()).addSymbol(Code.builder(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("x",AST.integer(666)))).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// E ::= (a) do y = 999; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Code.builder(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("y",AST.integer(999)))).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// D ::= F G  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("D").build()).addSymbol(Nonterminal.builder("F").build()).addSymbol(Nonterminal.builder("G").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= A do println(x); A do println(y); when x == 999 && y == 666  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Code.builder(Nonterminal.builder("A").build(),AST.stat(AST.println(AST.var("x")))).build()).addSymbol(Conditional.builder(Code.builder(Nonterminal.builder("A").build(),AST.stat(AST.println(AST.var("y")))).build(),AST.and(AST.equal(AST.var("x"),AST.integer(666)),AST.equal(AST.var("y"),AST.integer(999)))).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// C ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("C").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= D E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("B").build()).addSymbol(Nonterminal.builder("D").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         grammar = new DesugarState().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a;aa;aa;aa;a");

         Map<String, Object> inits = new HashMap<>();
         inits.put("x", 0);
         inits.put("y", 0);
         // inits.put("z", 0);
         
         Nonterminal start = null;
         for (Nonterminal nonterminal : grammar.getNonterminals()) {
        	 if (nonterminal.getName().equals("S")) {
        		 start = nonterminal;
        		 break;
        	 }
         }

        IguanaParser parser = new IguanaParser(grammar);
        boolean result = parser.parse(input, Nonterminal.withName("S"));

        assertTrue(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
    }
}
