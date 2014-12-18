package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A + A | a
 * 
 * B ::= b | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */

public class HiddenLeftRecursion {
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");

		Rule r1 = new Rule(A, list(B, A, Character.from('+'), A));
		Rule r2 = new Rule(A, list(Character.from('a')));
		
		Rule r3 = new Rule(B, list(Character.from('b')));
		Rule r4 = new Rule(B);
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ba+a+a");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
	}

}
