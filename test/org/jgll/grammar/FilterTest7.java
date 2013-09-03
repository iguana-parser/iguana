package org.jgll.grammar;

import static org.jgll.util.collections.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 *   E = E E+ 					(non-assoc)
 *     > (E + E | : E - E)  	(right)
 *     | a
 *     
 *      
 * @author Ali Afroozeh
 *
 */
public class FilterTest7 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");

		Nonterminal E = new Nonterminal("E");

		// E ::= E E+
		Rule rule1 = new Rule(E, list(E, new Nonterminal("E+", true)));
		builder.addRule(rule1);

		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= : E - E
		Rule rule3 = new Rule(E, list(new Character(':'), E, new Character('-'), E));
		builder.addRule(rule3);
		
		// E ::= a
		Rule rule4 = new Rule(E, list(new Character('a')));
		builder.addRule(rule4);
		
		// E+ ::= E+ E
		Rule rule5 = new Rule(new Nonterminal("E+", true), list(new Nonterminal("E+", true), E));
		builder.addRule(rule5);
		
		// E+ ::= E
		Rule rule6 = new Rule(new Nonterminal("E+", true), list(E));
		builder.addRule(rule6);

//		// .E E+, E E+
//		builder.addPrecedencePattern(E, rule1, 0, rule1);
//		
//		// E .E+, E E+
//		builder.addPrecedencePattern(E, rule1, 1, rule1);
//		
//		// .E E+, E + E
//		builder.addPrecedencePattern(E, rule1, 0, rule2);
//		
//		// E .E+, E + E
//		builder.addPrecedencePattern(E, rule1, 1, rule2);
//		
//		// .E E+, : E - E
//		builder.addPrecedencePattern(E, rule1, 0, rule3);
//		
		// .E + E, E + E
		builder.addPrecedencePattern(E, rule2, 0, rule2);
//		
//		// .E + E, : E - E
//		builder.addPrecedencePattern(E, rule2, 2, rule3);
		
		builder.rewritePrecedenceRules();

		grammar = builder.build();
		System.out.println(grammar);
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	public void testAssociativityAndPriority() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString(":aaa-aa+aa+aaa"), grammar, "E");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf);
	}


}
