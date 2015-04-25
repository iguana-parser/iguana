package org.iguana.grammar;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Test;

public class TestHaskell {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/haskell/haskell"));
	
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
		org.iguana.util.Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
		System.out.println(result);
	}
	
}
