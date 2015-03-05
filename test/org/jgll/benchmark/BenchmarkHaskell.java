package org.jgll.benchmark;

import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.Haskell;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;

public class BenchmarkHaskell {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ghc-output";

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Haskell.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("Module"));
		
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, start).addDirectory(sourceDir, "hs", true).build().run();
	}
	
}
