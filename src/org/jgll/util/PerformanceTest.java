package org.jgll.util;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;

public class PerformanceTest {
	
	private Grammar grammar;
	
	private GrammarGraph grammarGraph;
	
	private Input input;
	
	private GLLParser parser;
	
	private int warmupCount;
	
	private int runCount;
	
	public PerformanceTest(Grammar grammar, Input input) {
		parser = ParserFactory.newParser(grammar.toGrammarGraph(), input);
	}
	
	public void run(String startSymbol) {
		try {
			parser.parse(input, grammarGraph, startSymbol);
		} catch (ParseError e) {
			e.printStackTrace();
		}
	}

}
