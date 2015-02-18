package org.jgll.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.JavaCharacterLevel;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

public class BenchmarkJava extends AbstractBenchmark {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
	private static Grammar grammar = JavaCharacterLevel.grammar;

	private static Nonterminal start = Start.from(Nonterminal.withName("CompilationUnit"));

	private static Configuration config = Configuration.DEFAULT;
	
	private static int warmupCount = 0;
	private static int runCount = 1;
	
	
	public static void main(String[] args) throws IOException {

		List<File> files = find(sourceDir, "java");
		
		System.out.println(BenchmarkUtil.header());
		
		for (File f : files) {
			Input input = Input.fromFile(f);
			
			for (int i = 0; i < warmupCount; i++) {
				GLLParser parser = ParserFactory.getParser(config, input, grammar);
				parser.parse(input, grammar, start);
			}
			
			for (int i = 0; i < runCount; i++) {
				GLLParser parser = ParserFactory.getParser(config, input, grammar);
				ParseResult result = parser.parse(input, grammar, start);
				System.out.println(f);
				if (result.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
				} else {
					System.out.println("Parse error " + result.asParseError());
				}
			}
		}
	}
}
