package org.iguana;

import static org.junit.Assert.*;

import java.util.function.Function;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.util.Input;
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
	public Function<GrammarGraph, ParseResult> expectedResult;
	
	@Test
	public void testParser() {
				
		ParseResult result = parser.parse(input, grammar, startSymbol);
		assertTrue(result.isParseSuccess());
		
		// Checking the parse tree if it exists
		ParseResult expected = expectedResult.apply(parser.getGrammarGraph());
		
		if (expected.asParseSuccess().getRoot() == null) {
			assertEquals(expected.asParseSuccess().getStatistics(), result.asParseSuccess().getStatistics());
		} else {
			assertEquals(expected, result);
		}			
	}
	
}
