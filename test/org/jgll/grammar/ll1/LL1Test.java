package org.jgll.grammar.ll1;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A a
 * 
 * A ::= B D
 * 
 * B ::= b | epsilon
 * 
 * D ::= d | epsilon
 * 
 * @author Ali Afroozeh
 * 
 */
public class LL1Test {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal D = Nonterminal.withName("D");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character d = Character.from('d');

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();

		Rule rule1 = Rule.builder(S).addSymbols(A, a).build();
		builder.addRule(rule1);

		Rule rule2 = Rule.builder(A).addSymbols(B, D).build();
		builder.addRule(rule2);

		Rule rule3 = Rule.builder(B).addSymbols(b).build();
		builder.addRule(rule3);

		Rule rule4 = Rule.builder(B).build();
		builder.addRule(rule4);

		Rule rule5 = Rule.builder(D).addSymbols(d).build();
		builder.addRule(rule5);

		Rule rule6 = Rule.builder(D).build();
		builder.addRule(rule6);

		grammar = builder.build();
	}
	
	@Test
	public void testNullable() {
		assertEquals(false, grammar.isNullable(S));
		assertEquals(true, grammar.isNullable(A));
		assertEquals(true, grammar.isNullable(B));
		assertEquals(true, grammar.isNullable(D));
	}
	
	@Test
	public void ll1Test() {
//		assertTrue(grammarGraph.isLL1SubGrammar(S));
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(D));
	}

	@Test
	public void testPredictSets() {
		// S ::= . A [a]
		assertEquals(set(d, b, a), grammar.getPredictionSet(Rule.builder(S).addSymbols(A, a).build(), 0));
		
		// A ::= . B D
		assertEquals(set(d, b, a, EOF.getInstance()), grammar.getPredictionSet(Rule.builder(A).addSymbols(B, D).build(), 0));
		
		// B ::= . [b]
		assertEquals(set(b), grammar.getPredictionSet(Rule.builder(B).addSymbols(b).build(), 0));

		// A ::= epsilon
		assertEquals(set(d, a, EOF.getInstance()), grammar.getPredictionSet(Rule.builder(B).build(), 1));
	}

	@Test
	public void test1() {
		Input input = Input.fromString("bda");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());

		SPPFNodeFactory factory = new SPPFNodeFactory(parser.getRegistry());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= A a .", 2, node1);
		NonterminalNode node3 = factory.createNonterminalNode("A", 0, 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= B D .", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("B ::= b .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("D", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("D ::= d .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("d", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 1);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		
		assertTrue(result.asParseSuccess().getRoot().deepEquals(node1));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		
		SPPFNodeFactory factory = new SPPFNodeFactory(parser.getRegistry());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node2 = factory.createPackedNode("S ::= A a .", 0, node1);
		NonterminalNode node3 = factory.createNonterminalNode("A", 0, 0, 0).init();
		PackedNode node4 = factory.createPackedNode("A ::= B D .", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("epsilon", 0, 0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("D", 0, 0, 0).init();
		PackedNode node9 = factory.createPackedNode("D ::= .", 0, node8);
		node9.addChild(node7);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node10 = factory.createTerminalNode("a", 0, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		node1.addChild(node2);
		
		assertTrue(result.asParseSuccess().getRoot().deepEquals(node1));
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("ba");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		
		SPPFNodeFactory factory = new SPPFNodeFactory(parser.getRegistry());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node2 = factory.createPackedNode("S ::= A a .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node4 = factory.createPackedNode("A ::= B D .", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("B ::= b .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("D", 0, 1, 1).init();
		PackedNode node9 = factory.createPackedNode("D ::= .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("epsilon", 1, 0);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("a", 1, 1);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		
		assertTrue(result.asParseSuccess().getRoot().deepEquals(node1));
	}

	@Test
	public void test4() {
		Input input = Input.fromString("da");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		
		SPPFNodeFactory factory = new SPPFNodeFactory(parser.getRegistry());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node2 = factory.createPackedNode("S ::= A a .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node4 = factory.createPackedNode("A ::= B D .", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("epsilon", 0, 0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("D", 0, 0, 1).init();
		PackedNode node9 = factory.createPackedNode("D ::= d .", 0, node8);
		TerminalNode node10 = factory.createTerminalNode("d", 0, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("a", 1, 1);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);

		assertTrue(result.asParseSuccess().getRoot().deepEquals(node1));
	}
	
}
