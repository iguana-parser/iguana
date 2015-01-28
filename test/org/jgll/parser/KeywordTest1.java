package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.regex.Sequence;
import org.jgll.util.Configuration;
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
	
	private Sequence<Character> ifKeyword = Sequence.from("if");
	private Nonterminal A = Nonterminal.withName("A");

	@Before
	public void init() {
		Rule r1 = Rule.withHead(A).addSymbol(ifKeyword).build();
		grammar = Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(ifKeyword), grammar.getFirstSet(A));
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("if");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, Nonterminal.withName("A"));
	}
	
}
