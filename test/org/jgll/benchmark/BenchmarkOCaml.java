package org.jgll.benchmark;

import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.OCaml;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;

public class BenchmarkOCaml {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ocaml";

	static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(OCaml.precedencePatterns(), OCaml.exceptPatterns()).transform(new EBNFToBNF().transform(OCaml.grammar)));

	static Start startSymbol = Start.from(Nonterminal.withName("CompiliationUnit"));
		
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, startSymbol).addDirectory(sourceDir, "ml", true).build().run();
	}
	
}
