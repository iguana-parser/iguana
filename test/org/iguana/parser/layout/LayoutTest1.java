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
 * S ::= A B
 * A ::= a
 * B ::= b
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest1 {

	private static Grammar getGrammar() {
		Character a = Character.from('a');
		Character b = Character.from('b');
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		
		Nonterminal L = Nonterminal.withName("L");
		
		Rule r1 = Rule.withHead(S).addSymbols(A, B).setLayout(L).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).setLayout(L).build();
		Rule r3 = Rule.withHead(B).addSymbol(b).setLayout(L).build();
		
		Rule layout = Rule.withHead(L).addSymbol(Character.from(' ')).build();
		
		return Grammar.builder().addRules(r1, r2, r3, layout).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a b");
		Grammar grammar = getGrammar();
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
	}
}
