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
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E z   1
 *     > x E   2
 *     > E w   3
 *     > y E   4
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest5 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E z
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Character('z')));
		builder.addRule(rule1);
		
		// E ::=  x E
		Rule rule2 = new Rule(E, list(new Character('x'), E));
		builder.addRule(rule2);
		
		// E ::= E w
		Rule rule3 = new Rule(E, list(E, new Character('w')));
		builder.addRule(rule3);
		
		// E ::= y E
		Rule rule4 = new Rule(E, list(new Character('y'), E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(new Character('a')));
		builder.addRule(rule5);
		
		// (E, .E z, x E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, .E z, y E) 
		builder.addPrecedencePattern(E, rule1, 0, rule4);
		
		// (E, x .E, E w)
		builder.addPrecedencePattern(E, rule2, 1, rule3);
		
		// (E, .E w, y E)
		builder.addPrecedencePattern(E, rule3, 0, rule4);
		
		builder.rewritePrecedencePatterns();
		grammar =  builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
		levelParser = ParserFactory.createLevelParser(grammar);
	}

	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("xawz"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("xawz"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}

}
