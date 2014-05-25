package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.*;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.util.*;
import org.junit.*;

/**
 * A ::= "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest1 {
	
	private GrammarGraph grammarGraph;

	Keyword ifKeyword = Keyword.from("if");
	Nonterminal A = new Nonterminal("A");

	@Before
	public void init() {
		Rule r1 = new Rule(A, ifKeyword);
		
		Grammar grammar = new Grammar();
		grammar.addRule(r1);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(ifKeyword), grammarGraph.getFirstSet(A));
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("if");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "A");
	}
	
}
