package org.jgll.grammar;

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
import org.junit.Test;

public class TestOCaml {
	
	static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(OCaml.precedencePatterns(), OCaml.exceptPatterns()).transform(new EBNFToBNF().transform(OCaml.grammar)));

	static Configuration config = Configuration.DEFAULT;
	
	static Start startSymbol = Start.from(Nonterminal.withName("TopLevel"));
	
	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/test.ml");
		GrammarGraph grammarGraph = grammar.toGrammarGraph(input, config);
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammarGraph, startSymbol);
//			org.jgll.util.Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
		System.out.println(result);
	}
	
}
