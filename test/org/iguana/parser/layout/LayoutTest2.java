package org.iguana.parser.layout;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Test;

/**
 * 
 * S ::= A B C
 * A ::= a
 * B ::= epsilon
 * C ::= c
 * 
 * L ::= ' '
 *     | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest2 {

	private static Grammar getGrammar() {
		Character a = Character.from('a');
		Character c = Character.from('c');
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		Nonterminal L = Nonterminal.withName("L");
		
		Rule r1 = Rule.withHead(S).addSymbols(A, B, C).setLayout(L).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).setLayout(L).build();
		Rule r3 = Rule.withHead(B).setLayout(L).build();
		Rule r4 = Rule.withHead(C).setLayout(L).addSymbol(c).build();
		
		Rule layout1 = Rule.withHead(L).addSymbol(Character.from(' ')).build();
		Rule layout2 = Rule.withHead(L).build();
		
		return Grammar.builder().addRules(r1, r2, r3, r4, layout1, layout2).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a c");
		Grammar grammar = getGrammar();
		System.out.println(grammar);
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
	}
}
