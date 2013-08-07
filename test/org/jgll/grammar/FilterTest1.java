package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

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
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest1 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E + E
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		builder.addFilter(E, rule1, 2, rule1);
		builder.addFilter(E, rule1, 0, rule2);
		
		builder.filter();

		grammar = builder.build();
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	@Test
	public void testAssociativityAndPriority() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}

}
