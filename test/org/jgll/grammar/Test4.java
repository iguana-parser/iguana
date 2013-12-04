package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test4 {

	private Grammar grammar;
	private GLLParser parser;
	
	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		Rule r1 = new Rule(A, list(a, B, c));
		Rule r2 = new Rule(A, list(C));
		Rule r3 = new Rule(B, list(b));
		Rule r4 = new Rule(C, list(a, C));
		Rule r5 = new Rule(C, list(c));
		
		grammar = new GrammarBuilder("test4").addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("A").isNullable());
		assertFalse(grammar.getNonterminalByName("B").isNullable());
		assertFalse(grammar.getNonterminalByName("C").isNullable());
	}
	
	@Test
	public void testLL1() {
		assertFalse(grammar.getNonterminalByName("A").isLL1());
		assertTrue(grammar.getNonterminalByName("B").isLL1());
		assertTrue(grammar.getNonterminalByName("C").isLL1());
	}
	
	@Test
	public void testRDParser() throws ParseError {
		parser.parse(Input.fromString("abc"), grammar, "A");
		parser.parse(Input.fromString("aaaac"), grammar, "A");
	}
	
}
	