package org.jgll;

import java.util.function.Function;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

public class AbstractParserTest {

	protected Grammar grammar;
	
	protected GLLParser parser;
	
	protected Input input;
	
	protected Function<GrammarRegistry, ParseResult> expectedResult;
	
	public AbstractParserTest(Configuration config, Input input, Grammar grammar, Function<GrammarRegistry, ParseResult> result) {
		this.parser = ParserFactory.getParser(config, input, grammar);
		this.input = input;
		this.grammar = grammar;
		this.expectedResult = result;
	}
	
}
