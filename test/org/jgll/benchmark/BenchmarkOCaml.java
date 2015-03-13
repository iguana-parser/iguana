package org.jgll.benchmark;

import java.io.File;
import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;

public class BenchmarkOCaml {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ocaml";
	
	static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Grammar.load(new File(""))));

	static Start startSymbol = Start.from(Nonterminal.withName("CompilationUnit"));
		
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, startSymbol).addDirectory(sourceDir, "ml", true).setTimeout(30).build().run();
	}
	
}
