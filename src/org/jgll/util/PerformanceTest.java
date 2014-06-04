package org.jgll.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

public class PerformanceTest {
	
	private int warmupCount;
	
	private int runCount;
	
	private final Input input;

	private final String startSymbol;

	private final Grammar grammar;
	
	public PerformanceTest(Grammar grammar, Input input, String startSymbol, int warmupCount, int runCount) {
		this.grammar = grammar;
		this.input = input;
		this.startSymbol = startSymbol;
		this.warmupCount = warmupCount;
		this.runCount = runCount;
	}
	
	public List<ParseResult> run() {

		List<ParseResult> results = new ArrayList<>();
		
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		GLLParser parser = null;
		
		for (int i = 0; i < warmupCount; i++) {
			parser = ParserFactory.newParser(grammar, input);
			parser.parse(input, grammarGraph, startSymbol);
		}
		
		for (int i = 0; i < runCount; i++) {			
			parser = ParserFactory.newParser(grammar, input);
			results.add(parser.parse(input, grammarGraph, startSymbol)); 
		}
		
		return results;
	}
	
	public static void main(String[] args) {
		String grammarPath = "file:///Users/aliafroozeh/java";
		String inputPath = "/Users/aliafroozeh/test.java";
		String startSymbol = "start[CompilationUnit]";
		int runCount = 1;
		int warmupCount = 1;		
		try {
			Grammar grammar = GrammarUtil.load(URI.create(grammarPath));
			PerformanceTest test = new PerformanceTest(grammar, Input.fromPath(inputPath), startSymbol, runCount, warmupCount);
			test.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
