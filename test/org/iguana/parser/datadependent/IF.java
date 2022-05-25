package org.iguana.parser.datadependent;

import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.regex.Char;
import org.iguana.regex.Seq;
import org.iguana.utils.input.Input;

import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IF {

    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// A ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("A").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// AB(a,b) ::= (c) do var x = a; do var y = b; if (x == 5) A do println(x); else B do println(y);  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("AB").addParameters("a","b").build()).addSymbol(new Code.Builder(new Code.Builder(new Terminal.Builder(Seq.builder(Char.builder(99).build()).build()).build(),AST.varDeclStat(AST.varDecl("x",AST.var("a")))).build(),AST.varDeclStat(AST.varDecl("y",AST.var("b")))).build()).addSymbol(new IfThenElse.Builder(AST.equal(AST.var("x"),AST.integer(5)),new Code.Builder(new Nonterminal.Builder("A").build(),AST.stat(AST.println(AST.var("x")))).build(),new Code.Builder(new Nonterminal.Builder("B").build(),AST.stat(AST.println(AST.var("y")))).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= AB(5,6)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("AB").apply(AST.integer(5),AST.integer(6)).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= (b)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("B").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(98).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         System.out.println(grammar);

         Input input = Input.fromString("ca");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
    }
}
