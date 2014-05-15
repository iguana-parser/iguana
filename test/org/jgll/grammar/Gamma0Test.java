package org.jgll.grammar;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
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
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
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
	public void testSPPF() throws ParseError {
		Input input = Input.fromString("aad");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "S");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node3 = new IntermediateNode(grammarGraph.getIntermediateNodeId(A, S), 0, 2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 1, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 2, 2);
		node6.addChild(node7);
		node6.addChild(node8);
		node3.addChild(node4);
		node3.addChild(node6);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 2, 1);
		node2.addChild(node3);
		node2.addChild(node9);
		PackedNode node10 = new PackedNode(grammarGraph.getPackedNodeId(S, a, S), 1, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 3);
		IntermediateNode node12 = new IntermediateNode(grammarGraph.getIntermediateNodeId(A,S), 1, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 1, 1, 2);
		node13.addChild(node7);
		node12.addChild(node13);
		node12.addChild(node8);
		node11.addChild(node12);
		node11.addChild(node9);
		node10.addChild(node5);
		node10.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
	
}
