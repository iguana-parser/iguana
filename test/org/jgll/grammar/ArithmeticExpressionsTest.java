package org.jgll.grammar;

import static org.jgll.util.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E + E
 *     | E * E
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

		GrammarBuilder builder = new GrammarBuilder("gamma2");

		// E ::= E + E
		Nonterminal E = new Nonterminal("E");
		Rule rule0 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule0);

		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, new Character('*'), E));
		builder.addRule(rule1);

		// E ::= a
		Rule rule2 = new Rule(E, list(new Character('a')));
		builder.addRule(rule2);

		builder.addPrecedencePattern(E, rule0, 2, rule0);
		builder.addPrecedencePattern(E, rule1, 0, rule0);
		builder.addPrecedencePattern(E, rule1, 2, rule0);
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		builder.rewritePrecedencePatterns();

		grammar = builder.build();
		levelParser = ParserFactory.levelParser(grammar);
		rdParser = ParserFactory.recursiveDescentParser(grammar);
	}

	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}

	@Test
	public void testParsers1() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}

	@Test
	public void testParsers2() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}

}
