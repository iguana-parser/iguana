package org.jgll.benchmark;

import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.JavaCharacterLevel;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;

public class BenchmarkJava {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(JavaCharacterLevel.grammar));
	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));

	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, start).addDirectory(sourceDir, "java", true).build().run();
	}
}
