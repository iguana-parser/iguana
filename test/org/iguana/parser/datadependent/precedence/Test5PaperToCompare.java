//package org.iguana.parser.datadependent.precedence;
//
//import iguana.regex.Char;
//import iguana.regex.Seq;
//import iguana.utils.input.Input;
//import org.iguana.grammar.Grammar;
//import org.iguana.grammar.precedence.OperatorPrecedence;
//import org.iguana.grammar.symbol.*;
//import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
//import org.iguana.parser.IguanaParser;
//import org.iguana.util.Configuration;
//import org.iguana.util.Configuration.EnvironmentImpl;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//
//import static junit.framework.TestCase.assertNotNull;
//import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SuppressWarnings("unused")
//public class Test5PaperToCompare {
//
//    @Test
//    public void test() throws FileNotFoundException {
//         Grammar grammar =
//
//Grammar.builder()
//
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (-) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (() E ())  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(41).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (+) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (+) E  {UNDEFINED,3,RIGHT_REC} PREC(3,3)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,true,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (/) E  {LEFT,4,LEFT_RIGHT_REC} PREC(4,4)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(47).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(4).setPrecedenceLevel(PrecedenceLevel.from(4,4,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
//// E ::= E (*) E  {LEFT,5,LEFT_RIGHT_REC} PREC(5,5)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(5).setPrecedenceLevel(PrecedenceLevel.from(5,5,-1,false,false,true,new Integer[]{3},false,new Integer[]{})).build())
//// E ::= (-) E  {UNDEFINED,6,RIGHT_REC} PREC(6,6)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(6).setPrecedenceLevel(PrecedenceLevel.from(6,6,6,true,false,true,new Integer[]{3},false,new Integer[]{})).build())
//// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//.build();
//
//         Grammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test5PaperToCompare"));
//
//         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
//
//         desugarPrecedenceAndAssociativity.setOP1();
//
//		 Grammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
//         grammar2 = new OperatorPrecedence(grammar2.getPrecedencePatterns(), grammar2.getExceptPatterns()).transform(grammar2);
//
//         desugarPrecedenceAndAssociativity.setOP2();
//
//         Grammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
//
//         System.out.println(grammar1.toStringWithOrderByPrecedence());
//         System.out.println(grammar2);
//         System.out.println(grammar3.toStringWithOrderByPrecedence());
//
//         Input input = Input.fromString("a*+a*a+a--a/a");
//
//        IguanaParser parser1 = new IguanaParser(grammar1, Configuration.builder().setEnvironmentImpl(EnvironmentImpl.TRIE).build());
//        assertNotNull(parser1.getParserTree(input));
//        assertEquals(0, parser1.getStatistics().getAmbiguousNodesCount());
//
//        IguanaParser parser2 = new IguanaParser(grammar2);
//        assertNotNull(parser2.getParserTree(input));
//        assertEquals(0, parser2.getStatistics().getAmbiguousNodesCount());
//
//        IguanaParser parser3 = new IguanaParser(grammar3);
//        assertNotNull(parser3.getParserTree(input));
//        assertEquals(0, parser3.getStatistics().getAmbiguousNodesCount());
//    }
//}
