package org.iguana.benchmark;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.JavaNaturalCharacterLevel;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;

public class BenchmarkJava {

	private static Grammar originalGrammar = Grammar.load(new File("grammars/java/java"));
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));
	
	private static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(JavaNaturalCharacterLevel.precedencePatterns(), JavaNaturalCharacterLevel.exceptPatterns()).transform(new EBNFToBNF().transform(originalGrammar)));

	@SuppressWarnings("unused")
	private static Grammar ddGrammar = new DesugarPrecedenceAndAssociativity().transform(new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar)));
	
	public static void main(String[] args) throws IOException {
		IguanaBenchmark.builder(grammar, start)
				       .addDirectory(sourceDir, "java", true)
				       .setRunCount(5)
				       .setWarmupCount(3)
				       .setTimeout(60)
				       .setRunGCInBetween(true)
				       .build().run();
		
	}
}