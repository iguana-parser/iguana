package org.iguana.parser.datadependent.precedence;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.PrecedenceLevel;
import org.iguana.grammar.symbol.Recursion;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class Test1_3 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// E ::= ('a')  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
// E ::= E ('-')  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
// E ::= E ('^') E  {RIGHT,2,LEFT_RIGHT_REC} PREC(2,2) 
.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.RIGHT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,true)).build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();
         // grammar = new EBNFToBNF().transform(grammar);
         System.out.println(grammar);

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a^a-");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseSuccess());

         // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/",
         //                   result.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
