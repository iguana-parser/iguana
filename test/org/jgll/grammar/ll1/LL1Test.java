package org.jgll.grammar.ll1;

import static org.jgll.util.CollectionsUtil.list;
import static org.jgll.util.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TokenSymbolNode;
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
	
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal D = new Nonterminal("D");
	private Character a = new Character('a');
	private Character b = new Character('b');
	private Character d = new Character('d');

	@Before
	public void init() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("DanglingElse", factory);

		Rule rule1 = new Rule(S, list(A, a));
		builder.addRule(rule1);

		Rule rule2 = new Rule(A, list(B, D));
		builder.addRule(rule2);

		Rule rule3 = new Rule(B, list(b));
		builder.addRule(rule3);

		Rule rule4 = new Rule(B);
		builder.addRule(rule4);

		Rule rule5 = new Rule(D, list(d));
		builder.addRule(rule5);

		Rule rule6 = new Rule(D);
		builder.addRule(rule6);

		grammar = builder.build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getHeadGrammarSlot("S").isNullable());
		assertTrue(grammar.getHeadGrammarSlot("A").isNullable());
		assertTrue(grammar.getHeadGrammarSlot("B").isNullable());
		assertTrue(grammar.getHeadGrammarSlot("D").isNullable());
	}
	
	@Test
	public void ll1Test() {
		assertTrue(grammar.isLL1SubGrammar(S));
		assertTrue(grammar.isLL1SubGrammar(A));
		assertTrue(grammar.isLL1SubGrammar(B));
		assertTrue(grammar.isLL1SubGrammar(D));
	}

	@Test
	public void testPredictSets() {
		// S ::= . A [a]
		assertEquals(set(d, b, a), grammar.getPredictionSetForAlternate(S, 0));
		
		// A ::= . B D
		assertEquals(set(d, b, a, EOF.getInstance()), grammar.getPredictionSetForAlternate(A, 0));
		
		// B ::= . [b]
		assertEquals(set(b), grammar.getPredictionSetForAlternate(B, 0));

		assertEquals(set(d, a, EOF.getInstance()), grammar.getPredictionSetForAlternate(B, 1));
	}

	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("bda");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 1, 0, 3);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(3, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(D), 2, 1, 2);
		TokenSymbolNode node6 = new TokenSymbolNode(4, 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		
		assertTrue(sppf.deepEquals(node1));
	}
	
	@Test
	public void test2() throws ParseError {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 1, 0, 1);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 0);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(D), 2, 0, 0);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(2, 0, 1);
		node1.addChild(node2);
		node1.addChild(node5);
		
		assertTrue(sppf.deepEquals(node1));
	}
	
	@Test
	public void test3() throws ParseError {
		Input input = Input.fromString("ba");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 1, 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(3, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(D), 2, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node6 = new TokenSymbolNode(2, 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		
		assertTrue(sppf.deepEquals(node1));
	}

	@Test
	public void test4() throws ParseError {
		Input input = Input.fromString("da");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 1, 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(D), 2, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(4, 0, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(2, 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		
		assertTrue(sppf.deepEquals(node1));
	}
	
}
