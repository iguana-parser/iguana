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
import org.jgll.sppf.NonterminalSymbolNode;
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
	
	private GrammarGraph grammarGraph;

	@Before
	public void createGrammar() {
		
		Grammar grammar = new Grammar();
		
		Rule r1 = new Rule(S, list(A, S, d));
		grammar.addRule(r1);
		
		Rule r2 = new Rule(S, list(B, S));
		grammar.addRule(r2);
		
		Rule r3 = new Rule(S);
		grammar.addRule(r3);
		
		Rule r4 = new Rule(A, list(a));
		grammar.addRule(r4);
		
		Rule r5 = new Rule(A, list(c));
		grammar.addRule(r5);
		
		Rule r6 = new Rule(B, list(a));
		grammar.addRule(r6);

		Rule r7 = new Rule(B, list(b));
		grammar.addRule(r7);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testNullables() {
		assertTrue(grammarGraph.getHeadGrammarSlot("S").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("B").isNullable());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(a, b, c, Epsilon.getInstance()), grammarGraph.getFirstSet(S));
		assertEquals(set(a, c), grammarGraph.getFirstSet(A));
		assertEquals(set(a, b), grammarGraph.getFirstSet(B));
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(a, b, c, d, EOF.getInstance()), grammarGraph.getFollowSet(A));
		assertEquals(set(d, EOF.getInstance()), grammarGraph.getFollowSet(S));
	}
	
	@Test
	public void testSPPF() {
		Input input = Input.fromString("aad");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(A, S), 0, 2);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node5 = factory.createTokenNode(a, 0, 1);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(S, 1, 2);
		NonterminalSymbolNode node7 = factory.createNonterminalNode(B, 1, 2);
		TokenSymbolNode node8 = factory.createTokenNode(a, 1, 1);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = factory.createNonterminalNode(S, 2, 2);
		node6.addChild(node7);
		node6.addChild(node9);
		node3.addChild(node4);
		node3.addChild(node6);
		TokenSymbolNode node10 = factory.createTokenNode(d, 2, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		PackedNode node11 = new PackedNode(grammarGraph.getPackedNodeId(S, B, S), 1, node1);
		NonterminalSymbolNode node12 = factory.createNonterminalNode(B, 0, 1);
		node12.addChild(node5);
		NonterminalSymbolNode node13 = factory.createNonterminalNode(S, 1, 3);
		IntermediateNode node14 = factory.createIntermediateNode(list(A, S), 1, 2);
		NonterminalSymbolNode node15 = factory.createNonterminalNode(A, 1, 2);
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
