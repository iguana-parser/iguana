package org.jgll.grammar;

import java.io.IOException;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Test;

public class TestJava {

	Grammar grammar = JavaCharacterLevel.grammar;

	Configuration config = Configuration.DEFAULT;
	
	@Test
	public void testKeywordExclusion() {
		Input input = Input.fromString("boolean");
		GLLParser parser = ParserFactory.getParser(config, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Identifier"));
		System.out.println(result);
	}
	
	@Test
	public void test1() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/test.java");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Start.from(Nonterminal.withName("CompilationUnit")));
		System.out.println(result);
	}
}
