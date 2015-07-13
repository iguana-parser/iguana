package org.iguana.parser.datadependent.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;

import static org.iguana.grammar.symbol.LayoutStrategy.*;

import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarState;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.*;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Visualization;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

import static org.iguana.util.CollectionsUtil.*;

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
.addRule(Rule.withHead(Nonterminal.builder("G").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// F ::= (a) do x = 666; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("F").build()).addSymbol(Code.builder(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build(),AST.stat(AST.assign("x",AST.integer(666)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// E ::= (a) do y = 999; (;)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Code.builder(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build(),AST.stat(AST.assign("y",AST.integer(999)))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// D ::= F G  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("D").build()).addSymbol(Nonterminal.builder("F").build()).addSymbol(Nonterminal.builder("G").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= A do println(x); A do println(y); when x == 999 && y == 666  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Code.builder(Nonterminal.builder("A").build(),AST.stat(AST.println(AST.var("x")))).build()).addSymbol(Conditional.builder(Code.builder(Nonterminal.builder("A").build(),AST.stat(AST.println(AST.var("y")))).build(),AST.and(AST.equal(AST.var("x"),AST.integer(666)),AST.equal(AST.var("y"),AST.integer(999)))).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// C ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("C").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// B ::= D E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("B").build()).addSymbol(Nonterminal.builder("D").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         grammar = new DesugarState().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a;aa;aa;aa;a");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/state/", graph);

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
         
         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, start, inits, false);

         Assert.assertTrue(result.isParseSuccess());

         Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/state/",
                           result.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
