package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
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

	@Before
	public void init() {
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		Rule r1 = new Rule(new Nonterminal("A"), iff);
		GrammarBuilder builder = new GrammarBuilder();
		builder.addRule(r1);
		builder.addRule(GrammarBuilder.fromKeyword(iff));
		
		grammar = builder.build();
		rdParser = ParserFactory.recursiveDescentParser(grammar);
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i')), grammar.getNonterminalByName("A").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(2, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		rdParser.parse(Input.fromString("if"), grammar, "A");
	}
	
}
