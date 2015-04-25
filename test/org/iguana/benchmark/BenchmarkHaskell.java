package org.iguana.benchmark;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;

public class BenchmarkHaskell {
	
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
	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, start)
				       .addDirectory(sourceDir, "hs", true)
				       .setRunCount(1)
				       .setTimeout(60)
				       .build().run();
		
	}
}
