package org.iguana.parser.datadependent;

import java.util.Arrays;
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
public class XML {

    @Test
    public void test() {
         Grammar grammar =

Grammar.builder()
.setLayout(Nonterminal.builder("L").build())
// Content ::= CharData (Element CharData)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Content").build()).addSymbol(Nonterminal.builder("CharData").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("Element").build(), Nonterminal.builder("CharData").build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// STag ::= (<) n:Name Attribute* (>) n.yield  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("STag").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Nonterminal.builder("Name").setLabel("n").build()).addSymbol(Star.builder(Nonterminal.builder("Attribute").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).addSymbol(Return.builder(AST.yield("n")).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// L ::= (\u0009-\\u000A | \\u000D | \u0020)*  !>>  (\u0009-\\u000A | \\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("L").build()).addSymbol(Star.builder(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// CharData ::= (A-Z | a-z)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("CharData").build()).addSymbol(Star.builder(Alt.builder(CharacterRange.builder(65, 90).build(), CharacterRange.builder(97, 122).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// ETag(s) ::= (< /) n:Name when n.yield == s (>)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("ETag").addParameters("s").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(47).build()).build()).build()).addSymbol(Conditional.builder(Nonterminal.builder("Name").setLabel("n").build(),AST.equal(AST.yield("n"),AST.var("s"))).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Attribute ::= (_)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Attribute").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Element ::= s:STag Content ETag(s.val)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Element").build()).addSymbol(Nonterminal.builder("STag").setLabel("s").build()).addSymbol(Nonterminal.builder("Content").build()).addSymbol(Nonterminal.builder("ETag").apply(AST.val("s")).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Name ::= (a-z) (a-z)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(Rule.withHead(Nonterminal.builder("Name").build()).addSymbol(Alt.builder(CharacterRange.builder(97, 122).build()).build()).addSymbol(Star.builder(Alt.builder(CharacterRange.builder(97, 122).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();

//         DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
//         desugarAlignAndOffside.doAlign();

//         grammar = desugarAlignAndOffside.transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new EBNFToBNF().transform(grammar);
         // System.out.println(grammar);

//         desugarAlignAndOffside.doOffside();
//         grammar = desugarAlignAndOffside.transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

//         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         // System.out.println(grammar.toStringWithOrderByPrecedence());

         grammar = new LayoutWeaver().transform(grammar);

         Input input = Input.fromString("<note> <to>John</to> <from>Alice</from> </note>");
         GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

         // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/", graph);

         GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
         ParseResult result = parser.parse(input, graph, Nonterminal.withName("Element"));

         Assert.assertTrue(result.isParseSuccess());

         Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/",
                           result.asParseSuccess().getRoot(), input);

         Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
    }
}
