package org.jgll.grammar;

import java.io.File;
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

	private static Grammar originalGrammar = Grammar.load(new File("grammars/ocaml/ocaml"));
	Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(originalGrammar.getPrecedencePatterns(), originalGrammar.getExceptPatterns()).transform(new EBNFToBNF().transform(originalGrammar)));

	static Configuration config = Configuration.DEFAULT;
	
	static Start startSymbol = Start.from(Nonterminal.withName("CompilationUnit"));
	
	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/test.ml");
		GrammarGraph grammarGraph = grammar.toGrammarGraph(input, config);
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.reset();
		grammarGraph.reset(input);
		ParseResult result = parser.parse(input, grammarGraph, startSymbol);
		System.out.println(result);
	}
	
}
