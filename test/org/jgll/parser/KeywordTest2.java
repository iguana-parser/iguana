package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
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
	
	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Keyword iff = Keyword.from("if");

	@Before
	public void init() {
		
		Rule r1 = Rule.builder(A).addSymbols(iff, B).build();
		Rule r2 = Rule.builder(B).addSymbol(Character.from('b')).build();
		
		grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(iff.toRule()).build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff), grammar.getFirstSet(A));
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ifb");
		GLLParser parser = ParserFactory.newParser();
		parser.parse(input, grammar, "A");
	}
	
}
