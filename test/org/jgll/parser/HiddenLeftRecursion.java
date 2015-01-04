package org.jgll.parser;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Configuration;
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

		Rule r1 = Rule.builder(A).addSymbols(B, A, Character.from('+'), A).build();
		Rule r2 = Rule.builder(A).addSymbols(Character.from('a')).build();
		
		Rule r3 = Rule.builder(B).addSymbols(Character.from('b')).build();
		Rule r4 = Rule.builder(B).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ba+a+a");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
	}

}
