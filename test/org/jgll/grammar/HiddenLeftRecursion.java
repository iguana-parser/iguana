package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.util.*;
import org.junit.*;

/**
 * 
 * A ::= B A + A | a
 * 
 * B ::= b | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */

public class HiddenLeftRecursion {
	
	private GrammarGraph grammarGraph;

	@Before
	public void createGrammar() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");

		Rule r1 = new Rule(A, list(B, A, Character.from('+'), A));
		Rule r2 = new Rule(A, list(Character.from('a')));
		
		Rule r3 = new Rule(B, list(Character.from('b')));
		Rule r4 = new Rule(B);
		
		grammarGraph = new Grammar().addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4).toGrammarGraph();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ba+a+a");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
	}

}
