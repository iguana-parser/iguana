package org.jgll.parser.datadependent.precedence;
import org.jgll.datadependent.ast.AST;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;

import static org.jgll.grammar.symbol.LayoutStrategy.*;

import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.*;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("unused")
public class Test6_1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

/**

E(l,r) ::= (a) {-1}
        | [1 >= r]E(1,0) (w) {1}
        | [2 >= l](x) E(0,2) {2}
        | [3 >= r]E(3,r) (z) {3}

S ::= E(0,0) {-1}

 */

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false)).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false)).build())
// E ::= E (w)  {UNDEFINED,1,LEFT_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false)).build())
// E ::= (x) E  {UNDEFINED,2,RIGHT_REC} PREC(2,2)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(120).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,2,false,true)).build())
// E ::= E (z)  {UNDEFINED,3,LEFT_REC} PREC(3,3)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(122).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,true,true)).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false)).build())
.build();
         // grammar = new EBNFToBNF().transform(grammar);
         System.out.println(grammar);

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("xawz"); // ((xa)w)z; not: x((aw)z)
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         assertTrue(result.isParseSuccess());
         
         // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/",
         //                   result.asParseSuccess().getRoot(), input);

         assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
