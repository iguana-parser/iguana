package org.iguana.benchmark;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;

public class BenchmarkCSharp {

	private static Grammar originalGrammar = Grammar.load(new File("grammars/csharp/csharp"));
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/CSharp/roslyn";
	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));
	
	private static Grammar ddGrammar = new DesugarPrecedenceAndAssociativity().transform(new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar)));

	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(ddGrammar, start)
				       .addDirectory(sourceDir, "cs", true)
				       .setRunCount(1)
				       .setTimeout(100)
				       .setRunGCInBetween(true)
				       .build().run();
		
	}
}