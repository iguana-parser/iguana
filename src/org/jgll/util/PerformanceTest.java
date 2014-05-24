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
	
	public PerformanceTest(int runCount) {
		this.runCount = runCount;
	}
	
	public void run(Grammar grammar, Input input, String startSymbol) {

		results = new ParseResult[runCount];
		
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		GLLParser parser = null;
		
		for (int i = 0; i < warmupCount; i++) {
			parser = ParserFactory.newParser(grammar.toGrammarGraph(), input);
			result = parser.parse(input, grammarGraph, startSymbol);
		}
		
		for (int i = 0; i < runCount; i++) {			
			parser = ParserFactory.newParser(grammar.toGrammarGraph(), input);
			result =  parser.parse(input, grammarGraph, startSymbol);
			results[i] = result;
		}
	}
	
	public ParseResult getResult() {
		return result;
	}
	
}
