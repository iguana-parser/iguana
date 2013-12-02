package org.jgll.grammar.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E * E
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class ArithmeticExpressionsTest {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("ArithmeticExpressions");

		Nonterminal E = new Nonterminal("E");

		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, new Character('*'), E));
		builder.addRule(rule1);
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);

		// (E * .E, E * E)
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		
		// (E * .E, E + E)
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (.E * E, E + E)
		builder.addPrecedencePattern(E, rule1, 2, rule2);
		
		// (E + .E, E + E)
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
//		builder.rewritePrecedencePatterns();
		
		grammar = builder.build();
		levelParser = ParserFactory.createLevelParser(grammar);
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}

	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}

	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a*a+a"), grammar, "E");
		Visualization.generateSPPFNodesUnPacked("/Users/ali/newoutput", sppf1, Input.fromString("a+a*a+a"));
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}

	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a*a"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 1);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 0);
		node3.addChild(node4);
		TerminalSymbolNode node5 = new TerminalSymbolNode(43, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 2, 5);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("E2 ::= E2 [*] . E1"), 2, 4);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(97, 2);
		node8.addChild(node9);
		TerminalSymbolNode node10 = new TerminalSymbolNode(42, 3);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 5);
		TerminalSymbolNode node12 = new TerminalSymbolNode(97, 4);
		node11.addChild(node12);
		node6.addChild(node7);
		node6.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
