package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
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

	private GrammarGraph grammarGraph;

	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal C = new Nonterminal("C");
	Character a = new Character('a');
	Character b = new Character('b');
	Character c = new Character('c');

	@Before
	public void createGrammar() {
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B, list(A));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		
		grammarGraph = new Grammar()
													  .addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4)
													  .addRule(r5).toGrammarGraph();
	}
	
	
	@Test
	public void testFirstFollowSets() {
		assertEquals(set(a, b), grammarGraph.getFirstSet(A));
		assertEquals(set(a, b), grammarGraph.getFirstSet(B));
		assertEquals(set(c), grammarGraph.getFirstSet(C));
		
		assertEquals(set(c, EOF.getInstance()), grammarGraph.getFollowSet(A));
		assertEquals(set(c, EOF.getInstance()), grammarGraph.getFollowSet(B));
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("bc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}

	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 2, 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node3 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 0, 1);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 1, 2);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 1, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}

}