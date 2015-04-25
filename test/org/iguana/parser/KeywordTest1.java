package org.iguana.parser;

import static org.iguana.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
	
	private Terminal ifKeyword = Terminal.from(Sequence.from("if"));
	private Nonterminal A = Nonterminal.withName("A");

	@Before
	public void init() {
		Rule r1 = Rule.withHead(A).addSymbol(ifKeyword).build();
		grammar = Grammar.builder().addRule(r1).build();
	}
	
//	@Test
//	public void testFirstSet() {
//		assertEquals(set(ifKeyword), grammar.getFirstSet(A));
//	}
	
	@Test
	public void test() {
		Input input = Input.fromString("if");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, Nonterminal.withName("A"));
	}
	
}
