package org.jgll.grammar;


import static org.jgll.util.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
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
	private GLLParser levelParser;
	private GLLParser rdParser;


	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma1");
		
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
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}
	
	@Test
	public void testNullables() {
		assertEquals(true, grammar.getNonterminalByName("S").isNullable());
		assertEquals(false, grammar.getNonterminalByName("A").isNullable());
		assertEquals(false, grammar.getNonterminalByName("B").isNullable());
	}
	
	@Test
	public void testLongestGrammarChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(a, b, c, Epsilon.getInstance()), grammar.getNonterminalByName("S").getFirstSet());
		assertEquals(set(a, c), grammar.getNonterminalByName("A").getFirstSet());
		assertEquals(set(a, b), grammar.getNonterminalByName("B").getFirstSet());
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(a, b, c, d, EOF.getInstance()), grammar.getNonterminalByName("A").getFollowSet());
		assertEquals(set(d, EOF.getInstance()), grammar.getNonterminalByName("S").getFollowSet());
	}
	
	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("aad"), grammar, "S");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("aad"), grammar, "S");
		Visualization.generateSPPFGraph("/Users/ali/output", getSPPF());
//		assertEquals(true, sppf1.deepEquals(sppf2));
	}

	@Test
	public void testSPPF() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("aad"), grammar, "S");
		assertEquals(true, sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= B S ."), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 0);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TerminalSymbolNode node8 = new TerminalSymbolNode(97, 1);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2);
		TerminalSymbolNode node10 = new TerminalSymbolNode(-2, 2);
		node9.addChild(node10);
		node6.addChild(node7);
		node6.addChild(node9);
		TerminalSymbolNode node11 = new TerminalSymbolNode(100, 2);
		node5.addChild(node6);
		node5.addChild(node11);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node12 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1);
		IntermediateNode node13 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		node14.addChild(node4);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 1, 2);
		node16.addChild(node8);
		node15.addChild(node16);
		node15.addChild(node9);
		node13.addChild(node14);
		node13.addChild(node15);
		node12.addChild(node13);
		node12.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node12);
		return node1;
	}
	
}
