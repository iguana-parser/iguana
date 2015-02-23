package org.jgll.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.Haskell;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

import com.google.common.collect.ImmutableSet;

public class BenchmarkHaskell extends AbstractBenchmark {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ghc-output";

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Haskell.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("Module"));
	
	private static Set<String> ignoreList = ImmutableSet.of("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/codeGen/StgCmmPrim.hs");

	private static Configuration config = Configuration.DEFAULT;
	
	private static int warmupCount = 0;
	private static int runCount = 1;
	
	
	public static void main(String[] args) throws IOException {

		List<File> files = find(sourceDir, "hs");

		GrammarGraph grammarGraph = grammar.toGrammarGraph(Input.empty(), config);
		
		System.out.println(BenchmarkUtil.header());
		
		for (File f : files) {
			
			if (ignoreList.contains(f.getAbsolutePath()))
				continue;
			
			Input input = Input.fromFile(f);
			GLLParser parser = ParserFactory.getParser(config, input, grammar);
			
			for (int i = 0; i < warmupCount; i++) {
				parser.reset();
				grammarGraph.reset(input);
				parser.parse(input, grammarGraph, start);
			}
			
			for (int i = 0; i < runCount; i++) {
				parser.reset();
				grammarGraph.reset(input);
				System.out.println(f);
				ParseResult result = parser.parse(input, grammarGraph, start);
				if (result.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
				} else {
					System.out.println("Parse error " + result.asParseError());
				}
			}
		}
	}
}
