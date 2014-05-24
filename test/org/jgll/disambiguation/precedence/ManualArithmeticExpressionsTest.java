package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
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

	private GrammarGraph grammarGraph;
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void createGrammar() {
		
		grammar = new Grammar();

		Nonterminal E = new Nonterminal("E");
		Nonterminal T = new Nonterminal("T");
		Nonterminal F = new Nonterminal("F");

		// E ::= E + T
		Rule rule1 = new Rule(E, list(E, Character.from('+'), T));
		grammar.addRule(rule1);
		
		// E ::= T
		Rule rule2 = new Rule(E, list(T));
		grammar.addRule(rule2);
		
		// T ::= T * F
		Rule rule3 = new Rule(T, list(T, Character.from('*'), F));
		grammar.addRule(rule3);
		
		// T ::= F
		Rule rule4 = new Rule(T, list(F));
		grammar.addRule(rule4);
		
		// F ::= a
		Rule rule5 = new Rule(F, list(Character.from('a')));
		grammar.addRule(rule5);

		grammarGraph = grammar.toGrammarGraph();
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("a*a+a");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "E");
		assertTrue(result.isParseSuccess());
		// TODO: add tree comparison text here.
	}
	
}
