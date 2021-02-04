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
//public class Test3_1_c {
//
//    @Test
//    public void test() throws FileNotFoundException {
//         Grammar grammar =
//
//Grammar.builder()
//
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (*) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (^) E  {RIGHT,3,LEFT_RIGHT_REC} PREC(3,3)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.RIGHT).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//.build();
//
//         Grammar grammar4 =
//
//Grammar.builder()
//
//// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E3 ::= E2  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E3").build()).addSymbol(Nonterminal.builder("E2").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
//// E3 ::= E3 (*) E2  {UNDEFINED,1,LEFT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E3").build()).addSymbol(Nonterminal.builder("E3").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E2").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
//// E1 ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E1").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E2 ::= E1 (^) E2  {UNDEFINED,1,RIGHT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E2").build()).addSymbol(Nonterminal.builder("E1").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("E2").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E2 ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E2").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E3  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E3").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
//// E ::= E (+) E3  {UNDEFINED,1,LEFT_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E3").build()).setRecursion(Recursion.LEFT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,false,true,false,new Integer[]{},false,new Integer[]{})).build())
//// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1)
//.addRule(Rule.withHead(Nonterminal.builder("S").build()).addSymbol(Nonterminal.builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,new Integer[]{},false,new Integer[]{})).build())
//.build();
//
//         Grammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/Test3_1_c"));
//
//         DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
//
//         desugarPrecedenceAndAssociativity.setOP1();
//
//		 Grammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
//         System.out.println(grammar1.toStringWithOrderByPrecedence());
//
//         grammar2 = new OperatorPrecedence(grammar2.getPrecedencePatterns(), grammar2.getExceptPatterns()).transform(grammar2);
//
//         System.out.println(grammar2);
//
//         desugarPrecedenceAndAssociativity.setOP2();
//
//         Grammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
//         System.out.println(grammar3.toStringWithOrderByPrecedence());
//
//         Input input = Input.fromString("a+a^a^a*a");
//         // Input input = Input.fromString("a^a^a*a+a");
//         // Input input = Input.fromString("a+a*a*a");
//
//        IguanaParser parser1 = new IguanaParser(grammar1);
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
//
//        IguanaParser parser4 = new IguanaParser(grammar4);
//        assertNotNull(parser1.getParserTree(input));
//        assertEquals(0, parser4.getStatistics().getAmbiguousNodesCount());
//
//    }
//}
