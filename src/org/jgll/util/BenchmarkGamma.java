package org.jgll.util;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

import com.google.common.testing.GcFinalization;

public class BenchmarkGamma {
	
	private static Grammar init() {
		Nonterminal S = Nonterminal.withName("S");
		Character b = Character.from('b');
		Grammar.Builder builder = new Grammar.Builder();
		builder.addRule(new Rule(S, list(S, S, S)));
		builder.addRule(new Rule(S, list(S, S)));
		builder.addRule(new Rule(S, list(b)));
		return builder.build();
	}
	
	public static void main(String[] args) {
		Grammar grammar = init();
		
		// Warmup
		for (int i = 1; i <= 10; i++) {
			GLLParser parser = ParserFactory.newParser();
			parser.parse(Input.fromString(getBs(i * 10)), grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		}
		GcFinalization.awaitFullGc();
		
		System.out.println(IguanaBenchmark.header());
		for (int i = 1; i <= 50; i++) {
			for (int j = 0; j < 10; j++) {
				GLLParser parser = ParserFactory.newParser();
				ParseResult res = parser.parse(Input.fromString(getBs(i * 10)), grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");

				if (res.isParseSuccess()) {
					System.out.println(IguanaBenchmark.format(res.asParseSuccess().getParseStatistics()));
				} else {
					System.out.println("Parse error");
				}
				parser = null;
				res = null;
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

}
