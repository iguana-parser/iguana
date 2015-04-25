package org.iguana.parser;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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

		Rule r1 = Rule.withHead(A).addSymbols(B, A, Character.from('+'), A).build();
		Rule r2 = Rule.withHead(A).addSymbols(Character.from('a')).build();
		
		Rule r3 = Rule.withHead(B).addSymbols(Character.from('b')).build();
		Rule r4 = Rule.withHead(B).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ba+a+a");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
	}

}
