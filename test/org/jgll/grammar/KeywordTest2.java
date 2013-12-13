package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertEquals;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BitSetUtil;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= "if" B
 * 
 * B ::= [b]
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest2 {
	
	private Grammar grammar;
	private GLLParser parser;
	
	Nonterminal A = new Nonterminal("A");
	Nonterminal B = new Nonterminal("B");
	Keyword iff = new Keyword("if", new int[] {'i', 'f'});

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(iff, B));
		Rule r2 = new Rule(B, new Character('b'));
		
		GrammarBuilder builder = new GrammarBuilder();
		builder.addRule(r1);
		builder.addRule(r2);
		builder.addRule(GrammarBuilder.fromKeyword(iff));
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(BitSetUtil.from(grammar.getTokenID(iff)), grammar.getNonterminalByName("A").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(2, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		parser.parse(Input.fromString("ifb"), grammar, "A");
	}
	
}
