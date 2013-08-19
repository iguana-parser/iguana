package org.jgll.grammar;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

/**
 * 
 * E ::= 0 E ^ E	(right)
 *     > 1 E + E	(left)
 *     > 2 - E
 *     | 3 a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest2 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	
	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		Nonterminal E = new Nonterminal("E");
		Rule rule0 = new Rule(E, list(E, new Character('^'), E));
		builder.addRule(rule0);
		
		Rule rule1 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		// left associative E + E
		builder.addFilter(E, rule1, 2, rule1);
		
		// + has higher priority than -
		builder.addFilter(E, rule1, 0, rule2);
		
		// right associative E ^ E
		builder.addFilter(E, rule0, 0, rule0);
		
		// ^ has higher priority than -
		builder.addFilter(E, rule0, 0, rule2);
		
		// ^ has higher priority than +
		builder.addFilter(E, rule0, 0, rule1);
		builder.addFilter(E, rule0, 2, rule1);
		
		builder.filter();
		
		grammar = builder.build();
		System.out.println(grammar);
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}
	

	@Test
	public void testAssociativityAndPriority() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}
	

}
