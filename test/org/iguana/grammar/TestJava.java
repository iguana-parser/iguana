package org.iguana.grammar;

import static org.junit.Assert.*;

import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Test;

public class TestJava {

	private static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(JavaNaturalCharacterLevel.precedencePatterns(), JavaNaturalCharacterLevel.exceptPatterns()).transform(new EBNFToBNF().transform(JavaNaturalCharacterLevel.grammar)));

	Configuration config = Configuration.DEFAULT;
	
	public void testKeywordExclusion() {
		Input input = Input.fromString("boolean");
		GLLParser parser = ParserFactory.getParser(config, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Identifier"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void test1() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/test.java");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Start.from(Nonterminal.withName("CompilationUnit")));
		System.out.println(result);
	}
}
