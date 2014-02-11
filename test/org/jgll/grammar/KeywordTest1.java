package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest1 {
	
	private Grammar grammar;
	private GLLParser rdParser;

	Keyword ifKeyword = new Keyword("if");

	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("A"), ifKeyword);
		GrammarBuilder builder = new GrammarBuilder();
		builder.addRule(r1);
		
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(grammar.getTokenID(ifKeyword)), grammar.getFirstSet(grammar.getNonterminalByName("A")));
	}
	
	@Test
	public void test() throws ParseError {
		rdParser.parse(Input.fromString("if"), grammar, "A");
	}
	
}
