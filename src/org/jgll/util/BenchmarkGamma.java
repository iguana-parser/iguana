package org.jgll.util;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

import com.google.common.testing.GcFinalization;

public class BenchmarkGamma {
	
	/**
	 * S ::= S S S | S S | b
	 */
	private static Grammar gamma2() {
		Nonterminal S = Nonterminal.withName("S");
		Character b = Character.from('b');
		Grammar.Builder builder = new Grammar.Builder();
		builder.addRule(new Rule(S, list(S, S, S)));
		builder.addRule(new Rule(S, list(S, S)));
		builder.addRule(new Rule(S, list(b)));
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
		builder.addRule(new Rule(E, list(E, plus, E)));
		builder.addRule(new Rule(E, list(a)));
		return builder.build();
	}
	
	public static void main(String[] args) {
		
		int warmupCount = 10;
		int runCount = 10;
		
		Grammar grammar = gamma2();
		String startSymbol = "S";
		GrammarGraph grammarGraph = grammar.toGrammarGraphWithoutFirstFollowChecks();
		
		// Warmup
		for (int i = 1; i <= warmupCount; i++) {
			GLLParser parser = ParserFactory.newParser();
			parser.parse(Input.fromString(getBs(420)), grammarGraph, startSymbol);
			grammarGraph.reset();
		}
		GcFinalization.awaitFullGc();
		
		System.out.println(IguanaBenchmark.header());
		for (int i = 1; i <= 80; i++) {
			for (int j = 0; j < runCount; j++) {
				GLLParser parser = ParserFactory.newParser();
				Input input = Input.fromString(getBs(i * 10));
				ParseResult res = parser.parse(input, grammarGraph, startSymbol);

				if (res.isParseSuccess()) {
					System.out.println(IguanaBenchmark.format(res.asParseSuccess().getParseStatistics()));
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
