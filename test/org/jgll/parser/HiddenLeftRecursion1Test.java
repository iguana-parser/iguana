package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A a
 *     | D A b
 *     | c
 * 
 * B ::= x | epsilon
 * 
 * D ::= y | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class HiddenLeftRecursion1Test {

	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal D = Nonterminal.withName("D");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	private Character x = Character.from('x');
	private Character y = Character.from('y');

	@Before
	public void init() {		
		Rule r1 = new Rule(A, list(B, A, a));
		Rule r2 = new Rule(A, list(D, A, b));
		Rule r3 = new Rule(A, list(c));
		Rule r4 = new Rule(B, list(x));
		Rule r5 = new Rule(B);
		Rule r6 = new Rule(D, list(y));
		Rule r7 = new Rule(D);
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4)
									.addRule(r5).addRule(r6).addRule(r7).build();
			
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("xca");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode1(parser.getRegistry())));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("ycb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode2(parser.getRegistry())));
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("cababaab");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode3(parser.getRegistry())));
	}
	
	@Test
	public void test4() {
		Input input = Input.fromString("xcabbbbb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode4(parser.getRegistry())));
	}
	
	@Test
	public void test5() {
		Input input = Input.fromString("ycaaaabaaaa");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode5(parser.getRegistry())));
	}
	
	private SPPFNode getSPPFNode1(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= B A a .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . a", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= B A . a", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("B ::= x .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("x", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node9 = factory.createPackedNode("A ::= c .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 1);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode2(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("D ::= y .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("y", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node9 = factory.createPackedNode("A ::= c .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("b", 2, 1);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode3(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 8).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 7, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 7).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 0).init();
		PackedNode node6 = factory.createPackedNode("D ::= .", 0, node5);
		node5.addChild(node6);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 7).init();
		PackedNode node8 = factory.createPackedNode("A ::= B A a .", 6, node7);
		IntermediateNode node9 = factory.createIntermediateNode("A ::= B A . a", 0, 6).init();
		PackedNode node10 = factory.createPackedNode("A ::= B A . a", 0, node9);
		NonterminalNode node11 = factory.createNonterminalNode("B", 0, 0).init();
		PackedNode node12 = factory.createPackedNode("B ::= .", 0, node11);
		node11.addChild(node12);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 6).init();
		PackedNode node14 = factory.createPackedNode("A ::= B A a .", 5, node13);
		IntermediateNode node15 = factory.createIntermediateNode("A ::= B A . a", 0, 5).init();
		PackedNode node16 = factory.createPackedNode("A ::= B A . a", 0, node15);
		NonterminalNode node17 = factory.createNonterminalNode("A", 0, 5).init();
		PackedNode node18 = factory.createPackedNode("A ::= D A b .", 4, node17);
		IntermediateNode node19 = factory.createIntermediateNode("A ::= D A . b", 0, 4).init();
		PackedNode node20 = factory.createPackedNode("A ::= D A . b", 0, node19);
		NonterminalNode node21 = factory.createNonterminalNode("A", 0, 4).init();
		PackedNode node22 = factory.createPackedNode("A ::= B A a .", 3, node21);
		IntermediateNode node23 = factory.createIntermediateNode("A ::= B A . a", 0, 3).init();
		PackedNode node24 = factory.createPackedNode("A ::= B A . a", 0, node23);
		NonterminalNode node25 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node26 = factory.createPackedNode("A ::= D A b .", 2, node25);
		IntermediateNode node27 = factory.createIntermediateNode("A ::= D A . b", 0, 2).init();
		PackedNode node28 = factory.createPackedNode("A ::= D A . b", 0, node27);
		NonterminalNode node29 = factory.createNonterminalNode("A", 0, 2).init();
		PackedNode node30 = factory.createPackedNode("A ::= B A a .", 1, node29);
		IntermediateNode node31 = factory.createIntermediateNode("A ::= B A . a", 0, 1).init();
		PackedNode node32 = factory.createPackedNode("A ::= B A . a", 0, node31);
		NonterminalNode node33 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node34 = factory.createPackedNode("A ::= c .", 0, node33);
		TerminalNode node35 = factory.createTerminalNode("c", 0, 1);
		node34.addChild(node35);
		node33.addChild(node34);
		node32.addChild(node11);
		node32.addChild(node33);
		node31.addChild(node32);
		TerminalNode node36 = factory.createTerminalNode("a", 1, 1);
		node30.addChild(node31);
		node30.addChild(node36);
		node29.addChild(node30);
		node28.addChild(node5);
		node28.addChild(node29);
		node27.addChild(node28);
		TerminalNode node37 = factory.createTerminalNode("b", 2, 1);
		node26.addChild(node27);
		node26.addChild(node37);
		node25.addChild(node26);
		node24.addChild(node11);
		node24.addChild(node25);
		node23.addChild(node24);
		TerminalNode node38 = factory.createTerminalNode("a", 3, 1);
		node22.addChild(node23);
		node22.addChild(node38);
		node21.addChild(node22);
		node20.addChild(node5);
		node20.addChild(node21);
		node19.addChild(node20);
		TerminalNode node39 = factory.createTerminalNode("b", 4, 1);
		node18.addChild(node19);
		node18.addChild(node39);
		node17.addChild(node18);
		node16.addChild(node11);
		node16.addChild(node17);
		node15.addChild(node16);
		TerminalNode node40 = factory.createTerminalNode("a", 5, 1);
		node14.addChild(node15);
		node14.addChild(node40);
		node13.addChild(node14);
		node10.addChild(node11);
		node10.addChild(node13);
		node9.addChild(node10);
		TerminalNode node41 = factory.createTerminalNode("a", 6, 1);
		node8.addChild(node9);
		node8.addChild(node41);
		node7.addChild(node8);
		node4.addChild(node5);
		node4.addChild(node7);
		node3.addChild(node4);
		TerminalNode node42 = factory.createTerminalNode("b", 7, 1);
		node2.addChild(node3);
		node2.addChild(node42);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode4(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 8).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 7, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 7).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 0).init();
		PackedNode node6 = factory.createPackedNode("D ::= .", 0, node5);
		node5.addChild(node6);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 7).init();
		PackedNode node8 = factory.createPackedNode("A ::= D A b .", 6, node7);
		IntermediateNode node9 = factory.createIntermediateNode("A ::= D A . b", 0, 6).init();
		PackedNode node10 = factory.createPackedNode("A ::= D A . b", 0, node9);
		NonterminalNode node11 = factory.createNonterminalNode("A", 0, 6).init();
		PackedNode node12 = factory.createPackedNode("A ::= D A b .", 5, node11);
		IntermediateNode node13 = factory.createIntermediateNode("A ::= D A . b", 0, 5).init();
		PackedNode node14 = factory.createPackedNode("A ::= D A . b", 0, node13);
		NonterminalNode node15 = factory.createNonterminalNode("A", 0, 5).init();
		PackedNode node16 = factory.createPackedNode("A ::= D A b .", 4, node15);
		IntermediateNode node17 = factory.createIntermediateNode("A ::= D A . b", 0, 4).init();
		PackedNode node18 = factory.createPackedNode("A ::= D A . b", 0, node17);
		NonterminalNode node19 = factory.createNonterminalNode("A", 0, 4).init();
		PackedNode node20 = factory.createPackedNode("A ::= D A b .", 3, node19);
		IntermediateNode node21 = factory.createIntermediateNode("A ::= D A . b", 0, 3).init();
		PackedNode node22 = factory.createPackedNode("A ::= D A . b", 0, node21);
		NonterminalNode node23 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node24 = factory.createPackedNode("A ::= B A a .", 2, node23);
		IntermediateNode node25 = factory.createIntermediateNode("A ::= B A . a", 0, 2).init();
		PackedNode node26 = factory.createPackedNode("A ::= B A . a", 1, node25);
		NonterminalNode node27 = factory.createNonterminalNode("B", 0, 1).init();
		PackedNode node28 = factory.createPackedNode("B ::= x .", 0, node27);
		TerminalNode node29 = factory.createTerminalNode("x", 0, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		NonterminalNode node30 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node31 = factory.createPackedNode("A ::= c .", 1, node30);
		TerminalNode node32 = factory.createTerminalNode("c", 1, 1);
		node31.addChild(node32);
		node30.addChild(node31);
		node26.addChild(node27);
		node26.addChild(node30);
		node25.addChild(node26);
		TerminalNode node33 = factory.createTerminalNode("a", 2, 1);
		node24.addChild(node25);
		node24.addChild(node33);
		node23.addChild(node24);
		node22.addChild(node5);
		node22.addChild(node23);
		node21.addChild(node22);
		TerminalNode node34 = factory.createTerminalNode("b", 3, 1);
		node20.addChild(node21);
		node20.addChild(node34);
		node19.addChild(node20);
		node18.addChild(node5);
		node18.addChild(node19);
		node17.addChild(node18);
		TerminalNode node35 = factory.createTerminalNode("b", 4, 1);
		node16.addChild(node17);
		node16.addChild(node35);
		node15.addChild(node16);
		node14.addChild(node5);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node36 = factory.createTerminalNode("b", 5, 1);
		node12.addChild(node13);
		node12.addChild(node36);
		node11.addChild(node12);
		node10.addChild(node5);
		node10.addChild(node11);
		node9.addChild(node10);
		TerminalNode node37 = factory.createTerminalNode("b", 6, 1);
		node8.addChild(node9);
		node8.addChild(node37);
		node7.addChild(node8);
		node4.addChild(node5);
		node4.addChild(node7);
		node3.addChild(node4);
		TerminalNode node38 = factory.createTerminalNode("b", 7, 1);
		node2.addChild(node3);
		node2.addChild(node38);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode5(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 11).init();
		PackedNode node2 = factory.createPackedNode("A ::= B A a .", 10, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . a", 0, 10).init();
		PackedNode node4 = factory.createPackedNode("A ::= B A . a", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0).init();
		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
		node5.addChild(node6);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 10).init();
		PackedNode node8 = factory.createPackedNode("A ::= B A a .", 9, node7);
		IntermediateNode node9 = factory.createIntermediateNode("A ::= B A . a", 0, 9).init();
		PackedNode node10 = factory.createPackedNode("A ::= B A . a", 0, node9);
		NonterminalNode node11 = factory.createNonterminalNode("A", 0, 9).init();
		PackedNode node12 = factory.createPackedNode("A ::= B A a .", 8, node11);
		IntermediateNode node13 = factory.createIntermediateNode("A ::= B A . a", 0, 8).init();
		PackedNode node14 = factory.createPackedNode("A ::= B A . a", 0, node13);
		NonterminalNode node15 = factory.createNonterminalNode("A", 0, 8).init();
		PackedNode node16 = factory.createPackedNode("A ::= B A a .", 7, node15);
		IntermediateNode node17 = factory.createIntermediateNode("A ::= B A . a", 0, 7).init();
		PackedNode node18 = factory.createPackedNode("A ::= B A . a", 0, node17);
		NonterminalNode node19 = factory.createNonterminalNode("A", 0, 7).init();
		PackedNode node20 = factory.createPackedNode("A ::= D A b .", 6, node19);
		IntermediateNode node21 = factory.createIntermediateNode("A ::= D A . b", 0, 6).init();
		PackedNode node22 = factory.createPackedNode("A ::= D A . b", 1, node21);
		NonterminalNode node23 = factory.createNonterminalNode("D", 0, 1).init();
		PackedNode node24 = factory.createPackedNode("D ::= y .", 0, node23);
		TerminalNode node25 = factory.createTerminalNode("y", 0, 1);
		node24.addChild(node25);
		node23.addChild(node24);
		NonterminalNode node26 = factory.createNonterminalNode("A", 1, 6).init();
		PackedNode node27 = factory.createPackedNode("A ::= B A a .", 5, node26);
		IntermediateNode node28 = factory.createIntermediateNode("A ::= B A . a", 1, 5).init();
		PackedNode node29 = factory.createPackedNode("A ::= B A . a", 1, node28);
		NonterminalNode node30 = factory.createNonterminalNode("B", 1, 1).init();
		PackedNode node31 = factory.createPackedNode("B ::= .", 1, node30);
		node30.addChild(node31);
		NonterminalNode node32 = factory.createNonterminalNode("A", 1, 5).init();
		PackedNode node33 = factory.createPackedNode("A ::= B A a .", 4, node32);
		IntermediateNode node34 = factory.createIntermediateNode("A ::= B A . a", 1, 4).init();
		PackedNode node35 = factory.createPackedNode("A ::= B A . a", 1, node34);
		NonterminalNode node36 = factory.createNonterminalNode("A", 1, 4).init();
		PackedNode node37 = factory.createPackedNode("A ::= B A a .", 3, node36);
		IntermediateNode node38 = factory.createIntermediateNode("A ::= B A . a", 1, 3).init();
		PackedNode node39 = factory.createPackedNode("A ::= B A . a", 1, node38);
		NonterminalNode node40 = factory.createNonterminalNode("A", 1, 3).init();
		PackedNode node41 = factory.createPackedNode("A ::= B A a .", 2, node40);
		IntermediateNode node42 = factory.createIntermediateNode("A ::= B A . a", 1, 2).init();
		PackedNode node43 = factory.createPackedNode("A ::= B A . a", 1, node42);
		NonterminalNode node44 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node45 = factory.createPackedNode("A ::= c .", 1, node44);
		TerminalNode node46 = factory.createTerminalNode("c", 1, 1);
		node45.addChild(node46);
		node44.addChild(node45);
		node43.addChild(node30);
		node43.addChild(node44);
		node42.addChild(node43);
		TerminalNode node47 = factory.createTerminalNode("a", 2, 1);
		node41.addChild(node42);
		node41.addChild(node47);
		node40.addChild(node41);
		node39.addChild(node30);
		node39.addChild(node40);
		node38.addChild(node39);
		TerminalNode node48 = factory.createTerminalNode("a", 3, 1);
		node37.addChild(node38);
		node37.addChild(node48);
		node36.addChild(node37);
		node35.addChild(node30);
		node35.addChild(node36);
		node34.addChild(node35);
		TerminalNode node49 = factory.createTerminalNode("a", 4, 1);
		node33.addChild(node34);
		node33.addChild(node49);
		node32.addChild(node33);
		node29.addChild(node30);
		node29.addChild(node32);
		node28.addChild(node29);
		TerminalNode node50 = factory.createTerminalNode("a", 5, 1);
		node27.addChild(node28);
		node27.addChild(node50);
		node26.addChild(node27);
		node22.addChild(node23);
		node22.addChild(node26);
		node21.addChild(node22);
		TerminalNode node51 = factory.createTerminalNode("b", 6, 1);
		node20.addChild(node21);
		node20.addChild(node51);
		node19.addChild(node20);
		node18.addChild(node5);
		node18.addChild(node19);
		node17.addChild(node18);
		TerminalNode node52 = factory.createTerminalNode("a", 7, 1);
		node16.addChild(node17);
		node16.addChild(node52);
		node15.addChild(node16);
		node14.addChild(node5);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node53 = factory.createTerminalNode("a", 8, 1);
		node12.addChild(node13);
		node12.addChild(node53);
		node11.addChild(node12);
		node10.addChild(node5);
		node10.addChild(node11);
		node9.addChild(node10);
		TerminalNode node54 = factory.createTerminalNode("a", 9, 1);
		node8.addChild(node9);
		node8.addChild(node54);
		node7.addChild(node8);
		node4.addChild(node5);
		node4.addChild(node7);
		node3.addChild(node4);
		TerminalNode node55 = factory.createTerminalNode("a", 10, 1);
		node2.addChild(node3);
		node2.addChild(node55);
		node1.addChild(node2);
		return node1;
	}
	
}