package org.jgll.benchmark;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.JavaCharacterLevel;
import org.jgll.grammar.JavaNaturalCharacterLevel;
import org.jgll.parser.datadependent.java.Java;
import org.jgll.grammar.Test1;
import org.jgll.grammar.Test2;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.parser.datadependent.java.Java;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

public class BenchmarkJava {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
//	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(JavaCharacterLevel.grammar));
	private static Nonterminal start = Start.from(Nonterminal.withName("E"));
	
	
//	private static Grammar grammar = Test2.grammar;

//	private static Grammar grammar = new LayoutWeaver().transform(new OperatorPrecedence(Test1.precedencePatterns(), Test1.exceptPatterns()).transform(new EBNFToBNF().transform(Test1.grammar)));

	private static Grammar grammar = new DesugarPrecedenceAndAssociativity().transform(new LayoutWeaver().transform(new EBNFToBNF().transform(Java.grammar)));

	
	public static void main(String[] args) throws IOException {
		System.out.println(grammar.toStringWithOrderByPrecedence());
		
//		Input input = Input.fromString("a+a*-a");
//        GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

        // Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/excepts/", graph);

//        GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
//        ParseResult result = parser.parse(input, graph, start);
//        
//        System.out.println(result.toString());
		
//		IguanaBenchmark.builder(grammar, start).addFile("/Users/aliafroozeh/test.java").setRunCount(1).build().run();
//		IguanaBenchmark.builder(grammar, start).addDirectory(sourceDir, "java", true).build().run();
		
	}
}