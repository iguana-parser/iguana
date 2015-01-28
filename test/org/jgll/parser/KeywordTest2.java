package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.regex.Sequence;
import org.jgll.util.Configuration;
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
	private Sequence<Character> iff = Sequence.from("if");

	@Before
	public void init() {
		
		Rule r1 = Rule.withHead(A).addSymbols(iff, B).build();
		Rule r2 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		
		grammar = Grammar.builder().addRule(r1).addRule(r2).addRule(iff.toRule()).build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff), grammar.getFirstSet(A));
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ifb");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, Nonterminal.withName("A"));
	}
	
}
