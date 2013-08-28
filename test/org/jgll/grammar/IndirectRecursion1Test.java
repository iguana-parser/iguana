package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import java.util.Set;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B C | a
 * 
 * B ::= A | b
 * 
 * C ::= c
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion1Test {

	private GrammarBuilder builder;
	private Grammar grammar;
	private GLLParser levelParser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(B, list(A));
		Rule r4 = new Rule(B, list(new Character('b')));
		Rule r5 = new Rule(C, list(new Character('c')));
		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4)
													  .addRule(r5);
		grammar = builder.build();
		levelParser = ParserFactory.levelParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("bc"), grammar, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testReachabilityGraph() {
		Set<HeadGrammarSlot> set = builder.getReachableNonterminals("A");
		assertTrue(set.contains(grammar.getNonterminalByName("A")));
		assertTrue(set.contains(grammar.getNonterminalByName("B")));
		
		set = builder.getReachableNonterminals("B");
		assertTrue(set.contains(grammar.getNonterminalByName("A")));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TerminalSymbolNode node3 = new TerminalSymbolNode(98, 0);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 2);
		TerminalSymbolNode node5 = new TerminalSymbolNode(99, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}

}