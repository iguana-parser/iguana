package org.jgll.parser;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
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
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(A, S), 0, 2);
		NonterminalNode node4 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node5 = factory.createTokenNode(a, 0, 1);
		node4.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(S, 1, 2);
		NonterminalNode node7 = factory.createNonterminalNode(B, 1, 2);
		TokenSymbolNode node8 = factory.createTokenNode(a, 1, 1);
		node7.addChild(node8);
		NonterminalNode node9 = factory.createNonterminalNode(S, 2, 2);
		node6.addChild(node7);
		node6.addChild(node9);
		node3.addChild(node4);
		node3.addChild(node6);
		TokenSymbolNode node10 = factory.createTokenNode(d, 2, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		PackedNode node11 = new PackedNode(grammarGraph.getPackedNodeId(S, B, S), 1, node1);
		NonterminalNode node12 = factory.createNonterminalNode(B, 0, 1);
		node12.addChild(node5);
		NonterminalNode node13 = factory.createNonterminalNode(S, 1, 3);
		IntermediateNode node14 = factory.createIntermediateNode(list(A, S), 1, 2);
		NonterminalNode node15 = factory.createNonterminalNode(A, 1, 2);
		node15.addChild(node8);
		node14.addChild(node15);
		node14.addChild(node9);
		node13.addChild(node14);
		node13.addChild(node10);
		node11.addChild(node12);
		node11.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
