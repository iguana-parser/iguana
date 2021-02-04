//package org.iguana.parser.datadependent.precedence;
//
//import iguana.regex.Char;
//import iguana.regex.Seq;
//import iguana.utils.input.Input;
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.symbol.*;
//import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parsetree.ParseTreeNode;
//import org.junit.Test;
//
//import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SuppressWarnings("unused")
//public class Test13 {
//
//    @Test
//    public void test() {
//         Grammar grammar =
//
//Grammar.builder()
//
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (-) E  {UNDEFINED,1,RIGHT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
//// E ::= E (*) E  {LEFT,3,LEFT_RIGHT_REC} PREC(3,3)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,-1,false,false,true,new Integer[]{1},false,new Integer[]{})).build())
//// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//.build();
//
//         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
//         System.out.println(grammar.toStringWithOrderByPrecedence());
//
//         Input input = Input.fromString("a*-a*a");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
//    }
//}
