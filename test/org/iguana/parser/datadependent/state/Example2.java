package org.iguana.parser.datadependent.state;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
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

@SuppressWarnings("unused")
public class Example2 {

    @Test
    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// A ::= (a) do x = 999; (a) do y = 2; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("A").build()).addSymbol(new Code.Builder(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("x",AST.integer(999)))).build()).addSymbol(new Code.Builder(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build(),AST.stat(AST.assign("y",AST.integer(2)))).build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// D ::= (d)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("D").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(100).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= A do var m = z; C B do var n = x; D when m == 0 && n == 999  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Code.Builder(new Nonterminal.Builder("A").build(),AST.varDeclStat(AST.varDecl("m",AST.var("z")))).build()).addSymbol(new Nonterminal.Builder("C").build()).addSymbol(new Code.Builder(new Nonterminal.Builder("B").build(),AST.varDeclStat(AST.varDecl("n",AST.var("x")))).build()).addSymbol(new Conditional.Builder(new Nonterminal.Builder("D").build(),AST.and(AST.equal(AST.var("m"),AST.integer(0)),AST.equal(AST.var("n"),AST.integer(999)))).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// C ::= (c)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("C").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(99).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= (b) do z = x; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("B").build()).addSymbol(new Code.Builder(new Terminal.Builder(Seq.builder(Char.builder(98).build()).build()).build(),AST.stat(AST.assign("z",AST.var("x")))).build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);

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
        ParseTreeNode result = parser.getParserTree(input);

        assertNotNull(result);
    }
}
