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
import org.jgll.sppf.TerminalSymbolNode;
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

	private Character a = Character.from('a');
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Character d = Character.from('d');

	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();

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
		assertTrue(grammar.isNullable(S));
		assertFalse(grammar.isNullable(A));
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
		PackedNode node2 = factory.createPackedNode("S ::= a S .", 1, node1);
		TerminalSymbolNode node3 = factory.createTokenNode("a", 0, 1);
		NonterminalNode node4 = factory.createNonterminalNode("S", 1, 3).init();
		PackedNode node5 = factory.createPackedNode("S ::= A S d .", 2, node4);
		IntermediateNode node6 = factory.createIntermediateNode("S ::= A S . d", 1, 2).init();
		PackedNode node7 = factory.createPackedNode("S ::= A S . d", 2, node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node9 = factory.createPackedNode("A ::= a .", 1, node8);
		TerminalSymbolNode node10 = factory.createTokenNode("a", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		NonterminalNode node11 = factory.createNonterminalNode("S", 2, 2).init();
		PackedNode node12 = factory.createPackedNode("S ::= .", 2, node11);
		node11.addChild(node12);
		node7.addChild(node8);
		node7.addChild(node11);
		node6.addChild(node7);
		TerminalSymbolNode node13 = factory.createTokenNode("d", 2, 1);
		node5.addChild(node6);
		node5.addChild(node13);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		PackedNode node14 = factory.createPackedNode("S ::= A S d .", 2, node1);
		IntermediateNode node15 = factory.createIntermediateNode("S ::= A S . d", 0, 2).init();
		PackedNode node16 = factory.createPackedNode("S ::= A S . d", 1, node15);
		NonterminalNode node17 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node18 = factory.createPackedNode("A ::= a .", 0, node17);
		node18.addChild(node3);
		node17.addChild(node18);
		NonterminalNode node19 = factory.createNonterminalNode("S", 1, 2).init();
		PackedNode node20 = factory.createPackedNode("S ::= a S .", 2, node19);
		node20.addChild(node10);
		node20.addChild(node11);
		node19.addChild(node20);
		node16.addChild(node17);
		node16.addChild(node19);
		node15.addChild(node16);
		node14.addChild(node15);
		node14.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
}
