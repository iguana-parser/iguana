package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest1 {
	
	private GrammarGraph grammarGraph;

	Keyword ifKeyword = new Keyword("if");
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
	public void test() throws ParseError {
		Input input = Input.fromString("if");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "A");
	}
	
}
