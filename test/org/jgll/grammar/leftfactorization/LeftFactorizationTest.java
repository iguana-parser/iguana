package org.jgll.grammar.leftfactorization;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.RawGrammarBank;
import org.junit.Test;


public class LeftFactorizationTest {

	@Test
	public void test() {
		GrammarBuilder builder = RawGrammarBank.arithmeticExpressions();
		
		builder.leftFactorize("E");
		
		Grammar grammar = builder.build();
		
		System.out.println(grammar);
	}
	
}
