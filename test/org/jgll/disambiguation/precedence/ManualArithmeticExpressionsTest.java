package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
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
		
		Grammar.Builder builder = new Grammar.Builder();

		Nonterminal E = Nonterminal.withName("E");
		Nonterminal T = Nonterminal.withName("T");
		Nonterminal F = Nonterminal.withName("F");

		// E ::= E + T
		Rule rule1 = new Rule(E, list(E, Character.from('+'), T));
		builder.addRule(rule1);
		
		// E ::= T
		Rule rule2 = new Rule(E, list(T));
		builder.addRule(rule2);
		
		// T ::= T * F
		Rule rule3 = new Rule(T, list(T, Character.from('*'), F));
		builder.addRule(rule3);
		
		// T ::= F
		Rule rule4 = new Rule(T, list(F));
		builder.addRule(rule4);
		
		// F ::= a
		Rule rule5 = new Rule(F, list(Character.from('a')));
		builder.addRule(rule5);

		grammar = builder.build();
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("a*a+a");
		parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		// TODO: add tree comparison text here.
	}
	
}
