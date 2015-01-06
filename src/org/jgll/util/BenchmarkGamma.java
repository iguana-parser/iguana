package org.jgll.util;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration.LookupStrategy;

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
		
		int warmupCount = 0;
		int runCount = 5;
		
		Grammar grammar = gamma2();
		Nonterminal startSymbol = Nonterminal.withName("S");
		
//		StringWriter writer = new StringWriter();
//		grammarGraph.generate(new PrintWriter(writer));
//		System.out.println(writer.toString());
//		Class<?> clazz = CompilationUtil.getClass("test", "Test", writer.toString());
		
		Input input = Input.fromString(getBs(300));
		Configuration config = Configuration.builder().setDescriptorLookupStrategy(LookupStrategy.GLOBAL).build();
		
		// Warmup
		for (int i = 1; i <= warmupCount; i++) {
			GLLParser parser = ParserFactory.getParser(config, input, grammar);
//			GLLParser parser = (GLLParser) clazz.newInstance();
			parser.parse(input, grammar, startSymbol);
			parser.reset();
		}
		GcFinalization.awaitFullGc();
		
		System.out.println(BenchmarkUtil.header());
		for (int i = 1; i <= 30; i++) {
			for (int j = 0; j < runCount; j++) {
				input = Input.fromString(getBs(i * 10));
				GLLParser parser = ParserFactory.getParser(config, input, grammar);
				
//				GLLParser parser = (GLLParser) clazz.newInstance();
				
				ParseResult res = parser.parse(input, grammar, startSymbol);

				if (res.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(input, res.asParseSuccess().getParseStatistics()));
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
	
	private static String getAs(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("a");
		for(int i = 0; i < size; i++) {
			sb.append("+a");
		}
		return sb.toString();
	}

}
