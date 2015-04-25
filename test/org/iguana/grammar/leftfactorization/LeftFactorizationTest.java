package org.iguana.grammar.leftfactorization;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.LeftFactorize;
import org.junit.Test;


/**
 * 
 * E ::= E * E
 *     | E + E
 *     | a
 * 
 * 
 * E ::= E ((* E) | (+ E))
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class LeftFactorizationTest {

	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character star = Character.from('*');
	private Character plus = Character.from('+');

	@Test
	public void test() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E * E
		Rule rule1 = Rule.withHead(E).addSymbols(E, star, E).build();
		builder.addRule(rule1);
		
		
		// E ::= E + E
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule3);
		

		LeftFactorize lf = new LeftFactorize();
		lf.transform(builder.build());
	}
	
}
