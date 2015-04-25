package org.jgll.parser.datadependent.offside;

import java.util.Arrays;
import org.jgll.datadependent.ast.AST;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import static org.jgll.grammar.symbol.LayoutStrategy.*;
import org.jgll.grammar.transformation.DesugarAlignAndOffside;
import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.*;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

@SuppressWarnings("unused")
public class Test2_1 {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()
.setLayout(Nonterminal.builder("Layout").build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Exp ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
// Exp ::= Exp (+) (a)  {UNDEFINED,1,LEFT_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,false)).build())
// Stat ::= (x) (=) Exp  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Stat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(120).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Layout ::= WhiteSpace*  !>>  (\u0009-\\u000A | \u000C-\\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Layout").build()).addSymbol(Star.builder(Nonterminal.builder("WhiteSpace").build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// WhiteSpace ::= (\u0009-\\u000A | \u000C-\\u000D | \u001A | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("WhiteSpace").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(26, 26).build(), CharacterRange.builder(32, 32).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// S ::= align offside Stat+  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Align.builder(Plus.builder(Offside.builder(Nonterminal.builder("Stat").build()).build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
         desugarAlignAndOffside.doAlign();

         grammar = desugarAlignAndOffside.transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         desugarAlignAndOffside.doOffside();
         grammar = desugarAlignAndOffside.transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new LayoutWeaver().transform(grammar);

         Input input = Input.fromString(  "  x = a;             \n"
                                        + "  x =                \n"
                                        + "       a +           \n"
                                        + "     a;              \n"
                                        + "  x = a + a;         \n"
                                        + "  x =                \n"
                                        + "         a           \n"
                                        + "        +            \n"
                                        + "       a             \n"
                                        + "      +              \n"
                                        + "     a               \n");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Start.from(Nonterminal.withName("S")));

         Assert.assertTrue(result.isParseSuccess());

         // Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/",
         //                   result.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
