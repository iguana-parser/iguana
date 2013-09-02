package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E E+    (non-assoc)
 *     > E + E
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest3 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;
	
	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E E+
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Nonterminal("E+", true)));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(new Nonterminal("E+", true), list(new Nonterminal("E+", true), E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(new Nonterminal("E+", true), list(E));
		builder.addRule(rule5);
		
		// (E ::= .E E+, E E+) 
		builder.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		builder.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		builder.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E .E, E + E)
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
		builder.rewritePrecedenceRules();
		
		grammar = builder.build();
		System.out.println(grammar);
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		assertEquals(true, sppf1.equals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaaaaaaaa"), grammar, "E");
		Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", sppf);
	}


}
