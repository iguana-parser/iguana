package org.jgll.parser.datadependent;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * A ::= 'a'
 * S ::= x = l:A
 *
 */

public class Test1 {
	
	private Grammar grammar;

	@Before
	public void init() {
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal S = Nonterminal.withName("S");
		
		Rule r1 = Rule.builder(A).addSymbol(Character.from('a')).build();
		Rule r2 = Rule.builder(S).addSymbol(Nonterminal.builder(A).applyTo().setLabel("l").setVariable("x").build()).build();
		
		grammar = Grammar.builder().addRules(r1, r2).build();
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
	}

}
