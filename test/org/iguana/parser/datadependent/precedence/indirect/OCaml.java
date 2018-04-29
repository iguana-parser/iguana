package org.iguana.parser.datadependent.precedence.indirect;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.util.IguanaRunner;
import org.iguana.util.ParseStatistics;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class OCaml {

	public static void main(String[] args) throws FileNotFoundException {
		Grammar grammar = Grammar.load(new File("/Users/afroozeh/JavaNat"));
		
		grammar = new EBNFToBNF().transform(grammar);
		
		DesugarPrecedenceAndAssociativity precedence = new DesugarPrecedenceAndAssociativity();
		precedence.setOP2();

		grammar = precedence.transform(grammar);
		// System.out.println(grammar.toString());
		
		grammar = new LayoutWeaver().transform(grammar);

		grammar = new DesugarStartSymbol().transform(grammar);

		Map<URI, List<ParseStatistics>> results = IguanaRunner.builder(grammar)
				.addFile("/Users/afroozeh/workspace/jdk7u-jdk/test/sun/nio/cs/EUC_TW_OLD.java")
//				.addFile("/Users/afroozeh/workspace/jdk7u-jdk/test/java/lang/annotation/UnitTest.java")
//				.addDirectory("/Users/afroozeh/workspace/jdk7u-jdk/src/share/classes/java", "java", true)
//				.setLimit(500)
				.setStart(grammar.getStartSymbol())
				.setWarmupCount(0)
				.setRunCount(1000)
//				.setRunGCInBetween(true)
				.build()
				.run();

		long sum = results.values().stream().flatMap(List::stream).mapToLong(ParseStatistics::getNanoTime).sum();
		System.out.println("Sum running time: " + sum / 1000_000);


//		try {
//			Thread.sleep(1000_000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

//		Input input = Input.fromFile(new File("/Users/afroozeh/workspace/iguana/test/org/iguana/parser/datadependent/precedence/indirect/Test.ml"));
//
//		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
//
//        // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/indirect/", graph);
//
//        ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("CompilationUnit"));
//
//        Assert.assertTrue(result.isParseSuccess());
//
//        // Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/indirect/",
//        //                   result.asParseSuccess().getResult(), input);
//
//        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//
//        System.out.println(result.asParseSuccess().getStatistics());
		
	}

}
