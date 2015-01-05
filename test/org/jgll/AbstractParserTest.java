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
import org.junit.runners.Parameterized.Parameter;

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
		assertEquals(expectedResult.apply(parser.getRegistry()), result);
	}
	
}
