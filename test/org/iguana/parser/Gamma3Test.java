package org.iguana.parser;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 *  S ::= S S S S S
 *      | S S S S
 *      | S S S
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma3Test {
	
	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Character b = Character.from('b');
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = Rule.withHead(S).addSymbols(S, S, S, S, S).build();
		builder.addRule(rule1);
		
		Rule rule2 = Rule.withHead(S).addSymbols(S, S, S, S).build();
		builder.addRule(rule2);
		
		Rule rule3 = Rule.withHead(S).addSymbols(S, S, S).build();
		builder.addRule(rule3);
		
		Rule rule4 = Rule.withHead(S).addSymbols(b).build();
		builder.addRule(rule4);
		
		grammar = builder.build();
	}

	
	@Test
	public void test100bs() {
		Input input = Input.fromString(get100b());		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, Nonterminal.withName("S"));
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	

}
