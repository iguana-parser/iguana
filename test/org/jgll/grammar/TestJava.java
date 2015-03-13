package org.jgll.grammar;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

public class TestJava {

//	private static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(JavaNaturalCharacterLevel.precedencePatterns(), JavaNaturalCharacterLevel.exceptPatterns()).transform(new EBNFToBNF().transform(JavaNaturalCharacterLevel.grammar)));
	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(JavaNaturalCharacterLevel.grammar));

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
//		System.out.println(grammar.getConstructorCode());
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Start.from(Nonterminal.withName("CompilationUnit")));
//		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
		System.out.println(result);
	}
}
