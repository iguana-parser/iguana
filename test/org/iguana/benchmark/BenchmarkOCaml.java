package org.iguana.benchmark;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;

public class BenchmarkOCaml {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/ocaml/ocaml"));
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ocaml";
	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));
	
	private static Grammar ddGrammar = new DesugarPrecedenceAndAssociativity().transform(new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar)));

	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(ddGrammar, start)
				       .addDirectory(sourceDir, "ml", true)
				       .setRunCount(1)
				       .setTimeout(100)
				       .setRunGCInBetween(true)
				       .build().run();
		
	}	
}
