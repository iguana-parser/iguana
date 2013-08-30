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
 * E ::= E * E
 * 	   > E + E
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest6 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E * E
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Character('*'), E));
		builder.addRule(rule1);		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= - E
		Rule rule3 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule3);
		
		// E ::= a
		Rule rule4 = new Rule(E, list(new Character('a')));
		builder.addRule(rule4);
		
		
		// E * . E, E * E    
		// E * . E, E + E
		// . E * E, - E
		// . E * E, E + E
		// E + . E, E + E
		// . E + E, - E
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		builder.addPrecedencePattern(E, rule1, 2, rule2);
		builder.addPrecedencePattern(E, rule1, 0, rule3);
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		builder.addPrecedencePattern(E, rule2, 0, rule3);
		
		builder.rewritePrecedenceRules();

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
