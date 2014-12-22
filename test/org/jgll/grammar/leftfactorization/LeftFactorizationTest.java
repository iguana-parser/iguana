package org.jgll.grammar.leftfactorization;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.transformation.LeftFactorize;
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
		Rule rule1 = new Rule(E, list(E, star, E));
		builder.addRule(rule1);
		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		

		LeftFactorize lf = new LeftFactorize();
		lf.transform(builder.build());
	}
	
}
