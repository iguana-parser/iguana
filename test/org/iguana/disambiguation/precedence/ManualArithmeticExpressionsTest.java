package org.iguana.disambiguation.precedence;

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
		Rule rule1 = Rule.withHead(E).addSymbols(E, Character.from('+'), T).build();
		
		// E ::= T
		Rule rule2 = Rule.withHead(E).addSymbol(T).build();
		
		// T ::= T * F
		Rule rule3 = Rule.withHead(T).addSymbols(T, Character.from('*'), F).build();
		
		// T ::= F
		Rule rule4 = Rule.withHead(T).addSymbol(F).build();
		
		// F ::= a
		Rule rule5 = Rule.withHead(F).addSymbol(Character.from('a')).build();

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
