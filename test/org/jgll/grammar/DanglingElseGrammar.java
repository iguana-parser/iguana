package org.jgll.grammar;

import org.junit.Test;

/**
 * 
 * S ::= a S b S  ! a S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("DanglingElse");
		
		Nonterminal S = new Nonterminal("S");
		Terminal s = new Character('s');
		Terminal a = new Character('a');
		Terminal b = new Character('b');

		rule1 = new Rule(S, list(a, S, b, S)).popCondition(new Condition(list(a, S, b, S)));
		builder.addRule(rule1);
		
		rule2 = new Rule(S, list(a, S));
		builder.addRule(rule2);
		
		rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		// (S ::= a .S b S \ a S)
		builder.addFilter(S, rule1, 1, rule2);
		builder.filter();
		
		return builder.build();
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
	}

}
