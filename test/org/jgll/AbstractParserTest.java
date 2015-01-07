package org.jgll;

import static org.junit.Assert.*;

import java.util.function.Function;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.util.Input;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class AbstractParserTest {

	@Parameter(value = 0)
	public Input input;
	
	@Parameter(value = 1)
	public Grammar grammar;
	
	@Parameter(value = 2)
	public Nonterminal startSymbol;

	@Parameter(value = 3)
	public GLLParser parser;

	@Parameter(value = 4)
	public Function<GrammarRegistry, ParseResult> expectedResult;
	
	@Test
	public void testParser() {
				
		ParseResult result = parser.parse(input, grammar, startSymbol);
		System.out.println(input.length());
		System.out.println(parser.getConfiguration());
		System.out.println();
		assertTrue(result.isParseSuccess());
		
		// Checking the parse tree if it exists
		ParseResult expected = expectedResult.apply(parser.getRegistry());
		
		if (expected.asParseSuccess().getRoot() == null) {
			assertEquals(expected.asParseSuccess().getStatistics(), result.asParseSuccess().getStatistics());
		} else {
			assertEquals(expected, result);
		}			
	}
	
}
