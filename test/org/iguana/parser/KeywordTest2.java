package org.iguana.parser;

import static org.iguana.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
	private Terminal iff = Terminal.from(Sequence.from("if"));

	@Before
	public void init() {
		
		Rule r1 = Rule.withHead(A).addSymbols(iff, B).build();
		Rule r2 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		
		grammar = Grammar.builder().addRule(r1).addRule(r2).build();
	}
	
//	@Test
//	public void testFirstSet() {
//		assertEquals(set(iff), grammar.getFirstSet(A));
//	}
	
	@Test
	public void test() {
		Input input = Input.fromString("ifb");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, Nonterminal.withName("A"));
	}
	
}
