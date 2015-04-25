package org.iguana.parser.datadependent.offside;

import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;

import java.util.Arrays;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.PrecedenceLevel;
import org.iguana.grammar.symbol.Recursion;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Alt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

@SuppressWarnings("unused")
public class Test1_2 {

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
// S ::= offside Stat+  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Plus.builder(Offside.builder(Nonterminal.builder("Stat").build()).build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();
         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
         desugarAlignAndOffside.doOffside();
         
		 grammar = desugarAlignAndOffside.transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new LayoutWeaver().transform(grammar);

         Input input = Input.fromString("x = a;             \n" +
                                        "        x =        \n" +
                                        "        a +        \n" +
                                        "             a");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));

         Assert.assertTrue(result.isParseError());
    }
}
