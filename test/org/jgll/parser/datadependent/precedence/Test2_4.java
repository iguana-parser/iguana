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

@SuppressWarnings("unused")
public class Test2_4 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false)).build())
// E ::= (-) E  {UNDEFINED,1,RIGHT_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false)).build())
// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,false)).build())
// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2)
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,true,false)).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false)).build())
.build();
         // grammar = new EBNFToBNF().transform(grammar);
         System.out.println(grammar);

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a+-a");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         if (result.isParseSuccess()) {
             System.out.println("Success");

             // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/",
             //                   result.asParseSuccess().getRoot(), input);

         } else {
             System.out.println("Parse error!");
        }
    }
}
