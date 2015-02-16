package org.jgll.grammar;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Test;

public class TestJava {

	Grammar grammar = JavaCharacterLevel.grammar;
	
	@Test
	public void test1() {
		Input input = Input.fromString("class Test {}");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("CompilationUnit"));
		System.out.println(result);
	}
}
