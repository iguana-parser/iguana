package org.jgll.parser;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * S ::= A S d | B S | epsilon
 * A ::= a | c
 * B ::= a | b
 * 
 */
public class Gamma1Test {
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	private Character d = Character.from('d');
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = new Rule(S, list(A, S, d));
		builder.addRule(r1);
		
		Rule r2 = new Rule(S, list(B, S));
		builder.addRule(r2);
		
		Rule r3 = new Rule(S);
		builder.addRule(r3);
		
		Rule r4 = new Rule(A, list(a));
		builder.addRule(r4);
		
		Rule r5 = new Rule(A, list(c));
		builder.addRule(r5);
		
		Rule r6 = new Rule(B, list(a));
		builder.addRule(r6);

		Rule r7 = new Rule(B, list(b));
		builder.addRule(r7);
		
		grammar = builder.build();
	}
	
	@Test
	public void testNullables() {
		assertTrue(grammar.isNullable(S));
		assertFalse(grammar.isNullable(A));
		assertFalse(grammar.isNullable(B));
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(a, b, c, Epsilon.getInstance()), grammar.getFirstSet(S));
		assertEquals(set(a, c), grammar.getFirstSet(A));
		assertEquals(set(a, b), grammar.getFirstSet(B));
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(a, b, c, d, EOF.getInstance()), grammar.getFollowSet(A));
		assertEquals(set(d, EOF.getInstance()), grammar.getFollowSet(S));
	}
	
	@Test
	public void testSPPF() {
		Input input = Input.fromString("aad");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= A S d .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A S . d", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("S ::= A S . d", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("A ::= a .", 0, node5);
		TokenSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 1, 2).init();
		PackedNode node9 = factory.createPackedNode("S ::= B S .", 2, node8);
		NonterminalNode node10 = factory.createNonterminalNode("B", 1, 2).init();
		PackedNode node11 = factory.createPackedNode("B ::= a .", 1, node10);
		TokenSymbolNode node12 = factory.createTokenNode("a", 1, 1);
		node11.addChild(node12);
		node10.addChild(node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 2, 2).init();
		PackedNode node14 = factory.createPackedNode("S ::= .", 2, node13);
		node13.addChild(node14);
		node9.addChild(node10);
		node9.addChild(node13);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TokenSymbolNode node15 = factory.createTokenNode("d", 2, 1);
		node2.addChild(node3);
		node2.addChild(node15);
		PackedNode node16 = factory.createPackedNode("S ::= B S .", 1, node1);
		NonterminalNode node17 = factory.createNonterminalNode("B", 0, 1).init();
		PackedNode node18 = factory.createPackedNode("B ::= a .", 0, node17);
		node18.addChild(node7);
		node17.addChild(node18);
		NonterminalNode node19 = factory.createNonterminalNode("S", 1, 3).init();
		PackedNode node20 = factory.createPackedNode("S ::= A S d .", 2, node19);
		IntermediateNode node21 = factory.createIntermediateNode("S ::= A S . d", 1, 2).init();
		PackedNode node22 = factory.createPackedNode("S ::= A S . d", 2, node21);
		NonterminalNode node23 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node24 = factory.createPackedNode("A ::= a .", 1, node23);
		node24.addChild(node12);
		node23.addChild(node24);
		node22.addChild(node23);
		node22.addChild(node13);
		node21.addChild(node22);
		node20.addChild(node21);
		node20.addChild(node15);
		node19.addChild(node20);
		node16.addChild(node17);
		node16.addChild(node19);
		node1.addChild(node2);
		node1.addChild(node16);
		return node1;
	}
	
}
