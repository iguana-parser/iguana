package org.jgll.grammar;

import java.io.File;
import java.io.IOException;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.DesugarAlignAndOffside;
import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Test;

public class TestHaskell {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/haskell/haskell"));
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ghc-output/ghc";
	private static Nonterminal start = Start.from(Nonterminal.withName("Module"));
	
	private static Grammar grammar;

	
	static {
        DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
        desugarAlignAndOffside.doAlign();

        grammar = desugarAlignAndOffside.transform(originalGrammar);

        grammar = new EBNFToBNF().transform(grammar);

        desugarAlignAndOffside.doOffside();
        grammar = desugarAlignAndOffside.transform(grammar);

        grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);

        grammar = new LayoutWeaver().transform(grammar);
	}
	
	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/Test.hs");
		GrammarGraph grammarGraph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammarGraph, start);
		org.jgll.util.Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
		System.out.println(result);
	}
	
}
