package org.iguana.benchmark;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.iguana.datadependent.util.IguanaRunner;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.util.generator.GeneratorUtil;

public class BenchmarkGamma {

	static Nonterminal S = Nonterminal.withName("S");
	static Character b = Character.from('b');
	static Rule rule1 = Rule.withHead(S).addSymbols(S, S, S).build();
	static Rule rule2 = Rule.withHead(S).addSymbols(S, S).build();
	static Rule rule3 = Rule.withHead(S).addSymbols(b).build();
	static Grammar grammar = Grammar.builder().addRules(rule1, rule2, rule3).build();
	
	public static void main(String[] args) throws IOException {
		IguanaRunner.builder(grammar, S)
				       .addStrings(getStrings())
				       .setRunCount(5)
				       .setWarmupCount(3)
				       .setTimeout(60)
				       .setRunGCInBetween(true)
				       .build()
				       .run();
	}
	
	private static List<String> getStrings() {
		return IntStream.iterate(50, i -> i + 50).limit(6).mapToObj(i -> GeneratorUtil.repeat("b", i)).collect(Collectors.toList());
	}
	
}
