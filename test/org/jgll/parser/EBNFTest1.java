package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= A+
 *      
 * A ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class EBNFTest1 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		
		Rule rule1 = Rule.builder(S).addSymbols(Plus.from(A)).build();
		builder.addRule(rule1);
		Rule rule2 = Rule.builder(A).addSymbols(a).build();
		builder.addRule(rule2);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aaaaaa");
		GLLParser parser = ParserFactory.getParser();
		parser.parse(input, grammar, "S");
	}

}
