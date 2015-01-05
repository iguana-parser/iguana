package org.jgll.disambiguation.precedence;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E + T
 *     | T
 * 
 * T ::= T * F
 *     | F
 *     
 * F ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class ManualArithmeticExpressionsTest {

	private GLLParser parser;
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Nonterminal E = Nonterminal.withName("E");
		Nonterminal T = Nonterminal.withName("T");
		Nonterminal F = Nonterminal.withName("F");

		// E ::= E + T
		Rule rule1 = Rule.builder(E).addSymbols(E, Character.from('+'), T).build();
		
		// E ::= T
		Rule rule2 = Rule.builder(E).addSymbol(T).build();
		
		// T ::= T * F
		Rule rule3 = Rule.builder(T).addSymbols(T, Character.from('*'), F).build();
		
		// T ::= F
		Rule rule4 = Rule.builder(T).addSymbol(F).build();
		
		// F ::= a
		Rule rule5 = Rule.builder(F).addSymbol(Character.from('a')).build();

		grammar = Grammar.builder().addRules(rule1, rule2, rule3, rule4, rule5).build();
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("a*a+a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		// TODO: add tree comparison text here.
	}
	
}
