package org.jgll.grammar;


import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.EOF;
import org.jgll.grammar.symbols.Epsilon;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
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

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma1");
		
		Rule r1 = new Rule(new Nonterminal("S"), list(new Character('a'), new Nonterminal("S")));
		builder.addRule(r1);
		
		Rule r2 = new Rule(new Nonterminal("S"), list(new Nonterminal("A"), new Nonterminal("S"), new Character('d')));
		builder.addRule(r2);
		
		Rule r3 = new Rule(new Nonterminal("S"));
		builder.addRule(r3);
		
		Rule r4 = new Rule(new Nonterminal("A"), list(new Character('a')));
		builder.addRule(r4);
		
		grammar = builder.build();
		levelParser = ParserFactory.levelParser(grammar);
		rdParser = ParserFactory.recursiveDescentParser(grammar);
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
		assertEquals(set(new Character('a'), Epsilon.getInstance()), grammar.getNonterminalByName("S").getFirstSet());
		assertEquals(set(new Character('a')), grammar.getNonterminalByName("A").getFirstSet());
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(new Character('a'), new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("A").getFollowSet());
		assertEquals(set(new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("S").getFollowSet());
	}
	
	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("aad"), grammar, "S");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("aad"), grammar, "S");
		assertTrue(sppf1.deepEquals(sppf2));
	}

	@Test
	public void testSPPF() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aad"), grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= [a] S ."), 1, node1);
		TerminalSymbolNode node3 = new TerminalSymbolNode(97, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TerminalSymbolNode node7 = new TerminalSymbolNode(97, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2);
		node5.addChild(node6);
		node5.addChild(node8);
		TerminalSymbolNode node10 = new TerminalSymbolNode(100, 2);
		node4.addChild(node5);
		node4.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node4);
		PackedNode node11 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		node13.addChild(node3);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		node14.addChild(node7);
		node14.addChild(node8);
		node12.addChild(node13);
		node12.addChild(node14);
		node11.addChild(node12);
		node11.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
