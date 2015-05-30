package org.iguana.parser.semanticaction;

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
import org.iguana.util.SemanticAction;
import org.junit.Test;

/**
 * A ::= a { println("Hi"); }
 * 
 *
 */
public class Test1 {

	private static Grammar getGrammar() {
		SemanticAction action = SemanticAction.from(x -> System.out.println("Hi")); 
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		Rule r1 = Rule.withHead(A).addSymbol(a).setAction(action).build();
		return Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a");
		Grammar grammar = getGrammar();
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
	}

}
