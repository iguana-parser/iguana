package org.jgll.grammar;


import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.jgll.util.BitSetUtil;
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
	private GLLParser parser;
	
	private Character a = new Character('a');
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Character d = new Character('d');


	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma1");

		Rule r1 = new Rule(S, list(a, S));
		builder.addRule(r1);
		
		Rule r2 = new Rule(S, list(A, S, d));
		builder.addRule(r2);
		
		Rule r3 = new Rule(S);
		builder.addRule(r3);
		
		Rule r4 = new Rule(A, list(a));
		builder.addRule(r4);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullables() {
		assertTrue(grammar.getNonterminalByName("S").isNullable());
		assertFalse(grammar.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void testLongestGrammarChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(BitSetUtil.from(grammar.getTokenID(a), Epsilon.TOKEN_ID), grammar.getNonterminalByName("S").getFirstSet());
		assertEquals(BitSetUtil.from(grammar.getTokenID(a)), grammar.getNonterminalByName("A").getFirstSet());
	}

	@Test
	public void testFollowSets() {
		assertEquals(BitSetUtil.from(grammar.getTokenID(a), grammar.getTokenID(d), EOF.TOKEN_ID), grammar.getNonterminalByName("A").getFollowSet());
		assertEquals(BitSetUtil.from(grammar.getTokenID(d), EOF.TOKEN_ID), grammar.getNonterminalByName("S").getFollowSet());
	}
	
	@Test
	public void testSPPF() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("aad"), grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= [a] S ."), 1, node1);
		TokenSymbolNode node3 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 1, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2);
		node5.addChild(node6);
		node5.addChild(node8);
		TokenSymbolNode node9 = new TokenSymbolNode(3, 2, 1);
		node4.addChild(node5);
		node4.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node4);
		PackedNode node10 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1);
		IntermediateNode node11 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		node12.addChild(node3);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
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
