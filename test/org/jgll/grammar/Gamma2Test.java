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
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma2Test {
	
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		Rule rule1 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(new Nonterminal("S"), list(new Character('b')));
		builder.addRule(rule3);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
		
	@Test
	public void testParsers1() throws ParseError {
		//TODO: check against SPPF
		parser.parse(Input.fromString("bbb"), grammar, "S");
	}
	
	@Test
	public void testParsers2() throws ParseError {
		//TODO: check against SPPF
		parser.parse(Input.fromString("bbbbbbbbbb"), grammar, "S");
	}
	
	@Test
	public void test100bs() throws ParseError {
		Input input = Input.fromString(get100b());
		parser.parse(input, grammar, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}

}
