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
 *	S ::= a S 
 *      | A S d 
 *      | epsilon
 *       
 * 	A ::= a
 */
public class Gamma0Test {

	private GrammarGraph grammarGraph;
	
	private Character a = Character.from('a');
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Character d = Character.from('d');


	@Before
	public void init() {
		
		Grammar grammar = new Grammar();

		Rule r1 = new Rule(S, list(a, S));
		grammar.addRule(r1);
		
		Rule r2 = new Rule(S, list(A, S, d));
		grammar.addRule(r2);
		
		Rule r3 = new Rule(S);
		grammar.addRule(r3);
		
		Rule r4 = new Rule(A, list(a));
		grammar.addRule(r4);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testNullables() {
		assertTrue(grammarGraph.getHeadGrammarSlot("S").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(a, Epsilon.getInstance()), grammarGraph.getFirstSet(S));
		assertEquals(set(a), grammarGraph.getFirstSet(A));
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(a, d, EOF.getInstance()), grammarGraph.getFollowSet(A));
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
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, a, S), 1, node1);
		TokenSymbolNode node3 = factory.createTokenNode(a, 0, 1);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(S, 1, 3);
		IntermediateNode node5 = factory.createIntermediateNode(list(A, S), 1, 2);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(A, 1, 2);
		TokenSymbolNode node7 = factory.createTokenNode(a, 1, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(S, 2, 2);
		node5.addChild(node6);
		node5.addChild(node8);
		TokenSymbolNode node9 = factory.createTokenNode(d, 2, 1);
		node4.addChild(node5);
		node4.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node4);
		PackedNode node10 = new PackedNode(grammarGraph.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node11 = factory.createIntermediateNode(list(A, S), 0, 2);
		NonterminalSymbolNode node12 = factory.createNonterminalNode(A, 0, 1);
		node12.addChild(node3);
		NonterminalSymbolNode node13 = factory.createNonterminalNode(S, 1, 2);
		node13.addChild(node7);
		node13.addChild(node8);
		node11.addChild(node12);
		node11.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node9);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
	
}
