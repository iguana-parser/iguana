package org.jgll.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.generator.CompilationUtil;

import com.google.common.testing.GcFinalization;

public class BenchmarkGamma {
	
	/**
	 * S ::= S S S | S S | b
	 */
	private static Grammar gamma2() {
		Nonterminal S = Nonterminal.withName("S");
		Character b = Character.from('b');
		Grammar.Builder builder = new Grammar.Builder();
		builder.addRule(Rule.builder(S).addSymbols(S, S, S).build());
		builder.addRule(Rule.builder(S).addSymbols(S, S).build());
		builder.addRule(Rule.builder(S).addSymbol(b).build());
		return builder.build();
	}
	
	/**
	 * E ::= E + E | a
	 */
	private static Grammar expressions() {
		Nonterminal E = Nonterminal.withName("E");
		Character a = Character.from('a');
		Character plus = Character.from('+');
		Grammar.Builder builder = new Grammar.Builder();
		builder.addRule(Rule.builder(E).addSymbols(E, plus, E).build());
		builder.addRule(Rule.builder(E).addSymbol(a).build());
		return builder.build();
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		
		int warmupCount = 5;
		int runCount = 1;
		
		Grammar grammar = gamma2();
		String startSymbol = "S";
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		
//		StringWriter writer = new StringWriter();
//		grammarGraph.generate(new PrintWriter(writer));
//		System.out.println(writer.toString());
//		Class<?> clazz = CompilationUtil.getClass("test", "Test", writer.toString());
		
		// Warmup
		for (int i = 1; i <= warmupCount; i++) {
			GLLParser parser = ParserFactory.newParser();
//			GLLParser parser = (GLLParser) clazz.newInstance();
			parser.parse(Input.fromString(getBs(420)), grammarGraph, startSymbol);
			grammarGraph.reset();
		}
		GcFinalization.awaitFullGc();
		
		System.out.println(BenchmarkUtil.header());
		for (int i = 1; i <= 80; i++) {
			for (int j = 0; j < runCount; j++) {
				GLLParser parser = ParserFactory.newParser();
				
//				GLLParser parser = (GLLParser) clazz.newInstance();
				
				Input input = Input.fromString(getBs(i * 10));
				ParseResult res = parser.parse(input, grammarGraph, startSymbol);

				if (res.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(res.asParseSuccess().getParseStatistics()));
				} else {
					System.out.println("Parse error");
				}
				parser = null;
				res = null;
				grammarGraph.reset();
				GcFinalization.awaitFullGc();
			}
		}
	}
	
	
	private static String getBs(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private static String getAs(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("a");
		for(int i = 0; i < size; i++) {
			sb.append("+a");
		}
		return sb.toString();
	}

}
