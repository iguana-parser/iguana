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
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Example1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// A ::= a {x = 1;} a {y = 2;}
// A ::= (a) do x = 1; (a) do y = 2; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("A").build()).addSymbol(Code.builder(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("x",AST.integer(1)))).build()).addSymbol(Code.builder(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("y",AST.integer(2)))).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// D ::= (d)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("D").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(100).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= A do var m = 3; C B do var n = 4; D  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Code.builder(Nonterminal.builder("A").build(),AST.varDeclStat(AST.varDecl("m",AST.integer(3)))).build()).addSymbol(Nonterminal.builder("C").build()).addSymbol(Code.builder(Nonterminal.builder("B").build(),AST.varDeclStat(AST.varDecl("n",AST.integer(4)))).build()).addSymbol(Nonterminal.builder("D").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// C ::= (c)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("C").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(99).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= (b) do z = x; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("B").build()).addSymbol(Code.builder(Terminal.builder(Seq.builder(Char.builder(98).build()).build()).build(),AST.stat(AST.assign("z",AST.var("x")))).build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         grammar = new DesugarState().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("aa;cb;d");

         Map<String, Object> inits = new HashMap<>();
         inits.put("x", 0);
         inits.put("y", 0);
         inits.put("z", 0);
         
         Nonterminal start = null;
         for (Nonterminal nonterminal : grammar.getNonterminals()) {
        	 if (nonterminal.getName().equals("S")) {
        		 start = nonterminal;
        		 break;
        	 }
         }


        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.parse(input, Nonterminal.withName("S"));

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
    }
}
