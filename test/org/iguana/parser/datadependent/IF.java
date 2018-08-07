package org.iguana.parser.datadependent;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;

public class IF {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// A ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("A").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// AB(a,b) ::= (c) do var x = a; do var y = b; if (x == 5) A do println(x); else B do println(y);  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("AB").addParameters("a","b").build()).addSymbol(Code.builder(Code.builder(Terminal.builder(Seq.builder(Char.builder(99).build()).build()).build(),AST.varDeclStat(AST.varDecl("x",AST.var("a")))).build(),AST.varDeclStat(AST.varDecl("y",AST.var("b")))).build()).addSymbol(IfThenElse.builder(AST.equal(AST.var("x"),AST.integer(5)),Code.builder(Nonterminal.builder("A").build(),AST.stat(AST.println(AST.var("x")))).build(),Code.builder(Nonterminal.builder("B").build(),AST.stat(AST.println(AST.var("y")))).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= AB(5,6)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("AB").apply(AST.integer(5),AST.integer(6)).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= (b)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("B").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(98).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         System.out.println(grammar);

         Input input = Input.fromString("ca");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.parse(input, Nonterminal.withName("S"));

        assertNotNull(result);
    }
}
