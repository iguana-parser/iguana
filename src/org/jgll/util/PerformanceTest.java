package org.jgll.util;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

public class PerformanceTest {
	
	private int warmupCount;
	
	private int runCount;
	
	private ParseResult result;

	private ParseResult[] results;

	private final Input input;

	private final String startSymbol;

	private final Grammar grammar;
	
	public PerformanceTest(Grammar grammar, Input input, String startSymbol, int runCount) {
		this.grammar = grammar;
		this.input = input;
		this.startSymbol = startSymbol;
		this.runCount = runCount;
	}
	
	public void run() {

		results = new ParseResult[runCount];
		
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		GLLParser parser = null;
		
		for (int i = 0; i < warmupCount; i++) {
			parser = ParserFactory.newParser(grammar, input);
			result = parser.parse(input, grammarGraph, startSymbol);
		}
		
		for (int i = 0; i < runCount; i++) {			
			parser = ParserFactory.newParser(grammar, input);
			result =  parser.parse(input, grammarGraph, startSymbol);
			results[i] = result;
		}
	}
	
	public ParseResult getResult() {
		return result;
	}
	
	public static void main(String[] args) {
		
	}
	
}
