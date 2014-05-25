package org.jgll.parser;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.*;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.sppf.*;
import org.jgll.util.*;
import org.junit.*;


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
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	
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
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, B, S), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 3);
		IntermediateNode node6 = new IntermediateNode(grammarGraph.getIntermediateNodeId(A,S), 1, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 2, 1, 2);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 2, 2);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node11 = new PackedNode(grammarGraph.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node12 = new IntermediateNode(grammarGraph.getIntermediateNodeId(A,S), 0, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 2, 0, 1);
		node13.addChild(node4);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 2);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 1, 2);
		node15.addChild(node8);
		node14.addChild(node15);
		node14.addChild(node9);
		node12.addChild(node13);
		node12.addChild(node14);
		node11.addChild(node12);
		node11.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
