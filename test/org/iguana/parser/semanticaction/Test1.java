package org.iguana.parser.semanticaction;

import static org.junit.Assert.assertTrue;

import iguana.parsetrees.slot.Action;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.GLLParserImpl;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * A ::= a { println("Hi"); }
 * 
 *
 */
public class Test1 {

	private static Grammar getGrammar() {
        Action action = x -> { System.out.println(x); return null; };
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		Rule r1 = Rule.withHead(A).addSymbol(a).setAction(action).build();
		return Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a");
		Grammar grammar = getGrammar();
		GLLParser parser = new GLLParserImpl();
		ParseResult result = parser.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
	}

}
