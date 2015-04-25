package org.iguana.grammar;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
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
