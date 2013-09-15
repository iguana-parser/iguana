package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 * 
 * S ::= B C
 *     | D
 * 
 * E ::= 'e'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'd'
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TestRemovingUnsedNontemrinal {

	private Grammar grammar;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		Character b = new Character('b');
		Character c = new Character('c');
		Character d = new Character('d');
		Character e = new Character('e');
		Nonterminal S = new Nonterminal("S");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Nonterminal D = new Nonterminal("D");
		Nonterminal E = new Nonterminal("E");
		
		builder.addRule(new Rule(S, list(B, C)));
		builder.addRule(new Rule(S, list(D)));
		builder.addRule(new Rule(B, list(b)));
		builder.addRule(new Rule(C, list(c)));
		builder.addRule(new Rule(D, list(d)));
		builder.addRule(new Rule(E, list(e)));

		grammar = builder.removeUnusedNonterminals(S).build();
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
	}
	
	
	
}
