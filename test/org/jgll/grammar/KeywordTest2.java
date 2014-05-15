package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
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
 * A ::= "if" B
 * 
 * B ::= [b]
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest2 {
	
	private GrammarGraph grammarGraph;
	
	Nonterminal A = new Nonterminal("A");
	Nonterminal B = new Nonterminal("B");
	Keyword iff = Keyword.from("if");

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(iff, B));
		Rule r2 = new Rule(B, Character.from('b'));
		
		Grammar grammar = new Grammar();
		
		grammar.addRule(r1);
		grammar.addRule(r2);
		grammar.addRule(GrammarGraphBuilder.fromKeyword(iff));
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff), grammarGraph.getFirstSet(A));
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("ifb");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		parser.parse(input, grammarGraph, "A");
	}
	
}
