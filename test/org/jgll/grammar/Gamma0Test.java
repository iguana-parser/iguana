package org.jgll.grammar;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
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

	private Grammar grammar;
	
	private Character a = new Character('a');
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Character d = new Character('d');


	@Before
	public void init() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("gamma1", factory);

		Rule r1 = new Rule(S, list(a, S));
		builder.addRule(r1);
		
		Rule r2 = new Rule(S, list(A, S, d));
		builder.addRule(r2);
		
		Rule r3 = new Rule(S);
		builder.addRule(r3);
		
		Rule r4 = new Rule(A, list(a));
		builder.addRule(r4);
		
		grammar = builder.build();
	}
	
	@Test
	public void testNullables() {
		assertTrue(grammar.getHeadGrammarSlot("S").isNullable());
		assertFalse(grammar.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(a, Epsilon.getInstance()), grammar.getFirstSet(S));
		assertEquals(set(a), grammar.getFirstSet(A));
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(a, d, EOF.getInstance()), grammar.getFollowSet(A));
		assertEquals(set(d, EOF.getInstance()), grammar.getFollowSet(S));
	}
	
	@Test
	public void testSPPF() throws ParseError {
		Input input = Input.fromString("aad");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 3);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node3 = new IntermediateNode(grammar.getIntermediateNodeId(A, S), 0, 2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 2);
		node6.addChild(node7);
		node6.addChild(node8);
		node3.addChild(node4);
		node3.addChild(node6);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(d), 2, 1);
		node2.addChild(node3);
		node2.addChild(node9);
		PackedNode node10 = new PackedNode(grammar.getPackedNodeId(S, a, S), 1, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		IntermediateNode node12 = new IntermediateNode(grammar.getIntermediateNodeId(A,S), 1, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 1, 2);
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
