package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class LeftRecusriveWithEpsilonTest {
	
	private Grammar grammar;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(A);
		GrammarBuilder builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													  .addRule(r2)
													  .addRule(r3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
	}

}
