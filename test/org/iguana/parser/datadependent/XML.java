package org.iguana.parser.datadependent;

import iguana.regex.Alt;
import iguana.regex.Char;
import iguana.regex.CharRange;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.AST;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import java.util.Arrays;

import static iguana.utils.collections.CollectionsUtil.set;
import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;

@SuppressWarnings("unused")
public class XML {

    @Test
    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()
.setLayout(new Nonterminal.Builder("L").build())
// Content ::= CharData (Element CharData)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("Content").build()).addSymbol(new Nonterminal.Builder("CharData").build()).addSymbol(new Star.Builder(new Group.Builder(Arrays.asList(new Nonterminal.Builder("Element").build(), new Nonterminal.Builder("CharData").build())).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// STag ::= (<) n:Name Attribute* (>) n.yield  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("STag").build()).addSymbol(new Terminal.Builder(iguana.regex.Seq.builder(Char.builder(60).build()).build()).build()).addSymbol(new Nonterminal.Builder("Name").setLabel("n").build()).addSymbol(new Star.Builder(new Nonterminal.Builder("Attribute").build()).build()).addSymbol(new Terminal.Builder(iguana.regex.Seq.builder(Char.builder(62).build()).build()).build()).addSymbol(new Return.Builder(AST.yield("n")).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// L ::= (\u0009-\\u000A | \\u000D | \u0020)*  !>>  (\u0009-\\u000A | \\u000D | \u0020)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("L").build()).addSymbol(new Star.Builder(Terminal.from(Alt.builder(CharRange.builder(9, 10).build(), CharRange.builder(13, 13).build(), CharRange.builder(32, 32).build()).build())).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharRange.builder(9, 10).build(), CharRange.builder(13, 13).build(), CharRange.builder(32, 32).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// CharData ::= (A-Z | a-z)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("CharData").build()).addSymbol(Terminal.from(iguana.regex.Star.builder(Alt.builder(CharRange.builder(65, 90).build(), CharRange.builder(97, 122).build()).build()).build())).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// ETag(s) ::= (< /) n:Name when n.yield == s (>)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("ETag").addParameters("s").build()).addSymbol(new Terminal.Builder(iguana.regex.Seq.builder(Char.builder(60).build(), Char.builder(47).build()).build()).build()).addSymbol(new Conditional.Builder(new Nonterminal.Builder("Name").setLabel("n").build(),AST.equal(AST.yield("n"),AST.var("s"))).build()).addSymbol(new Terminal.Builder(iguana.regex.Seq.builder(Char.builder(62).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Attribute ::= (_)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("Attribute").build()).addSymbol(new Terminal.Builder(iguana.regex.Seq.builder(Char.builder(95).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Element ::= s:STag Content ETag(s.val)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("Element").build()).addSymbol(new Nonterminal.Builder("STag").setLabel("s").build()).addSymbol(new Nonterminal.Builder("Content").build()).addSymbol(new Nonterminal.Builder("ETag").apply(AST.val("s")).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// Name ::= (a-z) (a-z)*  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("Name").build()).addSymbol(Terminal.from(Alt.builder(CharRange.builder(97, 122).build()).build())).addSymbol(new Star.Builder(Terminal.from(Alt.builder(CharRange.builder(97, 122).build()).build())).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
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


        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
    }
}
