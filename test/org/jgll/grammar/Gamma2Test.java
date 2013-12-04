package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
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
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma2Test {
	
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		Rule rule1 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(new Nonterminal("S"), list(new Character('b')));
		builder.addRule(rule3);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
		
	@Test
	public void testParsers1() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("bbb"), grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	@Test
	public void testParsers2() throws ParseError {
		//TODO: check against SPPF
		parser.parse(Input.fromString("bbbbbbbbbb"), grammar, "S");
	}
	
	@Test
	public void test100bs() throws ParseError {
		Input input = Input.fromString(get100b());
		parser.parse(input, grammar, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 2, node1);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 0, 2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 1);
		TerminalSymbolNode node5 = new TerminalSymbolNode(98, 0);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		TerminalSymbolNode node7 = new TerminalSymbolNode(98, 1);
		node6.addChild(node7);
		node3.addChild(node4);
		node3.addChild(node6);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(98, 2);
		node8.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node8);
		PackedNode node10 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 2);
		node11.addChild(node4);
		node11.addChild(node6);
		node10.addChild(node11);
		node10.addChild(node8);
		PackedNode node12 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 1, node1);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		node13.addChild(node6);
		node13.addChild(node8);
		node12.addChild(node4);
		node12.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node10);
		node1.addChild(node12);
		return node1;
	}

}
