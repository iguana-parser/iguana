package org.jgll.grammar.leftfactorization;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.GrammarBank;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;


public class LeftFactorizationTest {

	@Test
	public void test() throws ParseError {
		GrammarBuilder builder = GrammarBank.arithmeticExpressions();
		
		builder.leftFactorize("E");
		
		Grammar grammar = builder.build();
		
		System.out.println(grammar);

		GLLParser rdParser = ParserFactory.createRecursiveDescentParser(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a*a"), grammar, "E");
	}
}
