package org.jgll.parser;

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
		Rule r1 = Rule.builder(A).addSymbols(B, A, a).build();
		Rule r2 = Rule.builder(A).addSymbols(D, A, b).build();
		Rule r3 = Rule.builder(A).addSymbols(c).build();
		Rule r4 = Rule.builder(B).addSymbols(x).build();
		Rule r5 = Rule.builder(B).build();
		Rule r6 = Rule.builder(D).addSymbols(y).build();
		Rule r7 = Rule.builder(D).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4)
									.addRule(r5).addRule(r6).addRule(r7).build();
			
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("xca");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode1(parser.getRegistry())));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("ycb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode2(parser.getRegistry())));
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("cababaab");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode3(parser.getRegistry())));
	}
	
	@Test
	public void test4() {
		Input input = Input.fromString("xcabbbbb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode4(parser.getRegistry())));
	}
	
	@Test
	public void test5() {
		Input input = Input.fromString("ycaaaabaaaa");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode5(parser.getRegistry())));
	}
	
	private SPPFNode getSPPFNode1(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= B A a .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . a", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= B A . a", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("B ::= x .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("x", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("A ::= c .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 3);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode2(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("D ::= y .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("y", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("A ::= c .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("b", 2, 3);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode3(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 8).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 7, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 7).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("D ::= .", 0, node5);
		TerminalNode node7 = factory.createEpsilonNode(0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 7).init();
		PackedNode node9 = factory.createPackedNode("A ::= B A a .", 6, node8);
		IntermediateNode node10 = factory.createIntermediateNode("A ::= B A . a", 0, 6).init();
		PackedNode node11 = factory.createPackedNode("A ::= B A . a", 0, node10);
		NonterminalNode node12 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node13 = factory.createPackedNode("B ::= .", 0, node12);
		node13.addChild(node7);
		node12.addChild(node13);
		NonterminalNode node15 = factory.createNonterminalNode("A", 0, 0, 6).init();
		PackedNode node16 = factory.createPackedNode("A ::= B A a .", 5, node15);
		IntermediateNode node17 = factory.createIntermediateNode("A ::= B A . a", 0, 5).init();
		PackedNode node18 = factory.createPackedNode("A ::= B A . a", 0, node17);
		NonterminalNode node20 = factory.createNonterminalNode("A", 0, 0, 5).init();
		PackedNode node21 = factory.createPackedNode("A ::= D A b .", 4, node20);
		IntermediateNode node22 = factory.createIntermediateNode("A ::= D A . b", 0, 4).init();
		PackedNode node23 = factory.createPackedNode("A ::= D A . b", 0, node22);
		NonterminalNode node25 = factory.createNonterminalNode("A", 0, 0, 4).init();
		PackedNode node26 = factory.createPackedNode("A ::= B A a .", 3, node25);
		IntermediateNode node27 = factory.createIntermediateNode("A ::= B A . a", 0, 3).init();
		PackedNode node28 = factory.createPackedNode("A ::= B A . a", 0, node27);
		NonterminalNode node30 = factory.createNonterminalNode("A", 0, 0, 3).init();
		PackedNode node31 = factory.createPackedNode("A ::= D A b .", 2, node30);
		IntermediateNode node32 = factory.createIntermediateNode("A ::= D A . b", 0, 2).init();
		PackedNode node33 = factory.createPackedNode("A ::= D A . b", 0, node32);
		NonterminalNode node35 = factory.createNonterminalNode("A", 0, 0, 2).init();
		PackedNode node36 = factory.createPackedNode("A ::= B A a .", 1, node35);
		IntermediateNode node37 = factory.createIntermediateNode("A ::= B A . a", 0, 1).init();
		PackedNode node38 = factory.createPackedNode("A ::= B A . a", 0, node37);
		NonterminalNode node40 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node41 = factory.createPackedNode("A ::= c .", 1, node40);
		TerminalNode node42 = factory.createTerminalNode("c", 0, 1);
		node41.addChild(node42);
		node40.addChild(node41);
		node38.addChild(node12);
		node38.addChild(node40);
		node37.addChild(node38);
		TerminalNode node43 = factory.createTerminalNode("a", 1, 2);
		node36.addChild(node37);
		node36.addChild(node43);
		node35.addChild(node36);
		node33.addChild(node5);
		node33.addChild(node35);
		node32.addChild(node33);
		TerminalNode node44 = factory.createTerminalNode("b", 2, 3);
		node31.addChild(node32);
		node31.addChild(node44);
		node30.addChild(node31);
		node28.addChild(node12);
		node28.addChild(node30);
		node27.addChild(node28);
		TerminalNode node45 = factory.createTerminalNode("a", 3, 4);
		node26.addChild(node27);
		node26.addChild(node45);
		node25.addChild(node26);
		node23.addChild(node5);
		node23.addChild(node25);
		node22.addChild(node23);
		TerminalNode node46 = factory.createTerminalNode("b", 4, 5);
		node21.addChild(node22);
		node21.addChild(node46);
		node20.addChild(node21);
		node18.addChild(node12);
		node18.addChild(node20);
		node17.addChild(node18);
		TerminalNode node47 = factory.createTerminalNode("a", 5, 6);
		node16.addChild(node17);
		node16.addChild(node47);
		node15.addChild(node16);
		node11.addChild(node12);
		node11.addChild(node15);
		node10.addChild(node11);
		TerminalNode node48 = factory.createTerminalNode("a", 6, 7);
		node9.addChild(node10);
		node9.addChild(node48);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node49 = factory.createTerminalNode("b", 7, 8);
		node2.addChild(node3);
		node2.addChild(node49);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode4(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 8).init();
		PackedNode node2 = factory.createPackedNode("A ::= D A b .", 7, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= D A . b", 0, 7).init();
		PackedNode node4 = factory.createPackedNode("A ::= D A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("D", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("D ::= .", 0, node5);
		TerminalNode node7 = factory.createEpsilonNode(0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 7).init();
		PackedNode node9 = factory.createPackedNode("A ::= D A b .", 6, node8);
		IntermediateNode node10 = factory.createIntermediateNode("A ::= D A . b", 0, 6).init();
		PackedNode node11 = factory.createPackedNode("A ::= D A . b", 0, node10);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 0, 6).init();
		PackedNode node14 = factory.createPackedNode("A ::= D A b .", 5, node13);
		IntermediateNode node15 = factory.createIntermediateNode("A ::= D A . b", 0, 5).init();
		PackedNode node16 = factory.createPackedNode("A ::= D A . b", 0, node15);
		NonterminalNode node18 = factory.createNonterminalNode("A", 0, 0, 5).init();
		PackedNode node19 = factory.createPackedNode("A ::= D A b .", 4, node18);
		IntermediateNode node20 = factory.createIntermediateNode("A ::= D A . b", 0, 4).init();
		PackedNode node21 = factory.createPackedNode("A ::= D A . b", 0, node20);
		NonterminalNode node23 = factory.createNonterminalNode("A", 0, 0, 4).init();
		PackedNode node24 = factory.createPackedNode("A ::= D A b .", 3, node23);
		IntermediateNode node25 = factory.createIntermediateNode("A ::= D A . b", 0, 3).init();
		PackedNode node26 = factory.createPackedNode("A ::= D A . b", 0, node25);
		NonterminalNode node28 = factory.createNonterminalNode("A", 0, 0, 3).init();
		PackedNode node29 = factory.createPackedNode("A ::= B A a .", 2, node28);
		IntermediateNode node30 = factory.createIntermediateNode("A ::= B A . a", 0, 2).init();
		PackedNode node31 = factory.createPackedNode("A ::= B A . a", 1, node30);
		NonterminalNode node32 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node33 = factory.createPackedNode("B ::= x .", 1, node32);
		TerminalNode node34 = factory.createTerminalNode("x", 0, 1);
		node33.addChild(node34);
		node32.addChild(node33);
		NonterminalNode node35 = factory.createNonterminalNode("A", 0, 1, 2).init();
		PackedNode node36 = factory.createPackedNode("A ::= c .", 2, node35);
		TerminalNode node37 = factory.createTerminalNode("c", 1, 2);
		node36.addChild(node37);
		node35.addChild(node36);
		node31.addChild(node32);
		node31.addChild(node35);
		node30.addChild(node31);
		TerminalNode node38 = factory.createTerminalNode("a", 2, 3);
		node29.addChild(node30);
		node29.addChild(node38);
		node28.addChild(node29);
		node26.addChild(node5);
		node26.addChild(node28);
		node25.addChild(node26);
		TerminalNode node39 = factory.createTerminalNode("b", 3, 4);
		node24.addChild(node25);
		node24.addChild(node39);
		node23.addChild(node24);
		node21.addChild(node5);
		node21.addChild(node23);
		node20.addChild(node21);
		TerminalNode node40 = factory.createTerminalNode("b", 4, 5);
		node19.addChild(node20);
		node19.addChild(node40);
		node18.addChild(node19);
		node16.addChild(node5);
		node16.addChild(node18);
		node15.addChild(node16);
		TerminalNode node41 = factory.createTerminalNode("b", 5, 6);
		node14.addChild(node15);
		node14.addChild(node41);
		node13.addChild(node14);
		node11.addChild(node5);
		node11.addChild(node13);
		node10.addChild(node11);
		TerminalNode node42 = factory.createTerminalNode("b", 6, 7);
		node9.addChild(node10);
		node9.addChild(node42);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node43 = factory.createTerminalNode("b", 7, 8);
		node2.addChild(node3);
		node2.addChild(node43);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode5(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 11).init();
		PackedNode node2 = factory.createPackedNode("A ::= B A a .", 10, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . a", 0, 10).init();
		PackedNode node4 = factory.createPackedNode("A ::= B A . a", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
		TerminalNode node7 = factory.createEpsilonNode(0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 10).init();
		PackedNode node9 = factory.createPackedNode("A ::= B A a .", 9, node8);
		IntermediateNode node10 = factory.createIntermediateNode("A ::= B A . a", 0, 9).init();
		PackedNode node11 = factory.createPackedNode("A ::= B A . a", 0, node10);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 0, 9).init();
		PackedNode node14 = factory.createPackedNode("A ::= B A a .", 8, node13);
		IntermediateNode node15 = factory.createIntermediateNode("A ::= B A . a", 0, 8).init();
		PackedNode node16 = factory.createPackedNode("A ::= B A . a", 0, node15);
		NonterminalNode node18 = factory.createNonterminalNode("A", 0, 0, 8).init();
		PackedNode node19 = factory.createPackedNode("A ::= B A a .", 7, node18);
		IntermediateNode node20 = factory.createIntermediateNode("A ::= B A . a", 0, 7).init();
		PackedNode node21 = factory.createPackedNode("A ::= B A . a", 0, node20);
		NonterminalNode node23 = factory.createNonterminalNode("A", 0, 0, 7).init();
		PackedNode node24 = factory.createPackedNode("A ::= D A b .", 6, node23);
		IntermediateNode node25 = factory.createIntermediateNode("A ::= D A . b", 0, 6).init();
		PackedNode node26 = factory.createPackedNode("A ::= D A . b", 1, node25);
		NonterminalNode node27 = factory.createNonterminalNode("D", 0, 0, 1).init();
		PackedNode node28 = factory.createPackedNode("D ::= y .", 1, node27);
		TerminalNode node29 = factory.createTerminalNode("y", 0, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		NonterminalNode node30 = factory.createNonterminalNode("A", 0, 1, 6).init();
		PackedNode node31 = factory.createPackedNode("A ::= B A a .", 5, node30);
		IntermediateNode node32 = factory.createIntermediateNode("A ::= B A . a", 1, 5).init();
		PackedNode node33 = factory.createPackedNode("A ::= B A . a", 1, node32);
		NonterminalNode node34 = factory.createNonterminalNode("B", 0, 1, 1).init();
		PackedNode node35 = factory.createPackedNode("B ::= .", 1, node34);
		TerminalNode node36 = factory.createEpsilonNode(1);
		node35.addChild(node36);
		node34.addChild(node35);
		NonterminalNode node37 = factory.createNonterminalNode("A", 0, 1, 5).init();
		PackedNode node38 = factory.createPackedNode("A ::= B A a .", 4, node37);
		IntermediateNode node39 = factory.createIntermediateNode("A ::= B A . a", 1, 4).init();
		PackedNode node40 = factory.createPackedNode("A ::= B A . a", 1, node39);
		NonterminalNode node42 = factory.createNonterminalNode("A", 0, 1, 4).init();
		PackedNode node43 = factory.createPackedNode("A ::= B A a .", 3, node42);
		IntermediateNode node44 = factory.createIntermediateNode("A ::= B A . a", 1, 3).init();
		PackedNode node45 = factory.createPackedNode("A ::= B A . a", 1, node44);
		NonterminalNode node47 = factory.createNonterminalNode("A", 0, 1, 3).init();
		PackedNode node48 = factory.createPackedNode("A ::= B A a .", 2, node47);
		IntermediateNode node49 = factory.createIntermediateNode("A ::= B A . a", 1, 2).init();
		PackedNode node50 = factory.createPackedNode("A ::= B A . a", 1, node49);
		NonterminalNode node52 = factory.createNonterminalNode("A", 0, 1, 2).init();
		PackedNode node53 = factory.createPackedNode("A ::= c .", 2, node52);
		TerminalNode node54 = factory.createTerminalNode("c", 1, 2);
		node53.addChild(node54);
		node52.addChild(node53);
		node50.addChild(node34);
		node50.addChild(node52);
		node49.addChild(node50);
		TerminalNode node55 = factory.createTerminalNode("a", 2, 3);
		node48.addChild(node49);
		node48.addChild(node55);
		node47.addChild(node48);
		node45.addChild(node34);
		node45.addChild(node47);
		node44.addChild(node45);
		TerminalNode node56 = factory.createTerminalNode("a", 3, 4);
		node43.addChild(node44);
		node43.addChild(node56);
		node42.addChild(node43);
		node40.addChild(node34);
		node40.addChild(node42);
		node39.addChild(node40);
		TerminalNode node57 = factory.createTerminalNode("a", 4, 5);
		node38.addChild(node39);
		node38.addChild(node57);
		node37.addChild(node38);
		node33.addChild(node34);
		node33.addChild(node37);
		node32.addChild(node33);
		TerminalNode node58 = factory.createTerminalNode("a", 5, 6);
		node31.addChild(node32);
		node31.addChild(node58);
		node30.addChild(node31);
		node26.addChild(node27);
		node26.addChild(node30);
		node25.addChild(node26);
		TerminalNode node59 = factory.createTerminalNode("b", 6, 7);
		node24.addChild(node25);
		node24.addChild(node59);
		node23.addChild(node24);
		node21.addChild(node5);
		node21.addChild(node23);
		node20.addChild(node21);
		TerminalNode node60 = factory.createTerminalNode("a", 7, 8);
		node19.addChild(node20);
		node19.addChild(node60);
		node18.addChild(node19);
		node16.addChild(node5);
		node16.addChild(node18);
		node15.addChild(node16);
		TerminalNode node61 = factory.createTerminalNode("a", 8, 9);
		node14.addChild(node15);
		node14.addChild(node61);
		node13.addChild(node14);
		node11.addChild(node5);
		node11.addChild(node13);
		node10.addChild(node11);
		TerminalNode node62 = factory.createTerminalNode("a", 9, 10);
		node9.addChild(node10);
		node9.addChild(node62);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node63 = factory.createTerminalNode("a", 10, 11);
		node2.addChild(node3);
		node2.addChild(node63);
		node1.addChild(node2);
		return node1;
	}
	
}