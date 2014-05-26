package org.jgll.grammar.leftfactorization;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarOperations;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.junit.Test;


public class LeftFactorizationTest {

	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character star = Character.from('*');
	private Character plus = Character.from('+');

	private Grammar grammar;

	@Test
	public void test() {
		
		grammar = new Grammar();
		
		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, star, E));
		grammar.addRule(rule1);
		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		grammar.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		grammar.addRule(rule3);
		

		GrammarOperations op = new GrammarOperations(grammar);
		Grammar g = op.leftFactorize();
		System.out.println(g);
	}
	
}
