package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= x A x
 *     | x
 * 
 * @author Ali Afroozeh
 * 
 */
public class Test14 {
	
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Nonterminal A = Nonterminal.withName("A");
		Character x = Character.from('x');
		
		Rule r1 = new Rule(A, list(x, A, x));
		Rule r2 = new Rule(A, list(x));

		grammar = new Grammar.Builder().addRule(r1).addRule(r2).build();
	}

	@Test
	public void test() throws Exception {
		System.out.println(BenchmarkUtil.header());
		for (int i = 3; i < 10000; i+=2) {
			Input input = Input.fromString(BenchmarkUtil.getChars('x', i));
			GLLParser parser = ParserFactory.newParser(grammar, input);
			GrammarGraph grammarGraph = grammar.toGrammarGraph();
			ParseResult result = parser.parse(input, grammarGraph, "A");
			System.out.println(BenchmarkUtil.format(result.asParseSuccess().getParseStatistics()));
			assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
			grammarGraph.reset();
		}
	}
	
}
