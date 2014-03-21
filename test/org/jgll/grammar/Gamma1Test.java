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
import org.jgll.util.ToJavaCode;
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
	
	private Character a = new Character('a');
	private Character b = new Character('b');
	private Character c = new Character('c');
	private Character d = new Character('d');
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("gamma1", factory);
		
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
		assertTrue(grammar.getHeadGrammarSlot("S").isNullable());
		assertFalse(grammar.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammar.getHeadGrammarSlot("B").isNullable());
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
	public void testSPPF() throws ParseError {
		Input input = Input.fromString("aad");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		System.out.println(ToJavaCode.toJavaCode(sppf, grammar));
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 3);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, B, S), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		IntermediateNode node6 = new IntermediateNode(grammar.getIntermediateNodeId(A,S), 1, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 1, 2);
		TokenSymbolNode node8 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 2);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammar.getRegularExpressionId(d), 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node11 = new PackedNode(grammar.getPackedNodeId(S, A, S, d), 2, node1);
		IntermediateNode node12 = new IntermediateNode(grammar.getIntermediateNodeId(A,S), 0, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 1);
		node13.addChild(node4);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 2);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 1, 2);
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
