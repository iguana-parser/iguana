package org.jgll.grammar;

import org.junit.Test;

/**
 * 
 * S ::= a S b S
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
		
		rule1 = new Rule(new Nonterminal("S"), list(new Character('a'), new Nonterminal("S"), new Character('b'), new Nonterminal("S")));
		builder.addRule(rule1);
		
		rule2 = new Rule(new Nonterminal("S"), list(new Character('a'), new Nonterminal("S")));
		builder.addRule(rule2);
		
		rule3 = new Rule(new Nonterminal("S"), list(new Character('s')));
		builder.addRule(rule3);
		
		return builder.build();
	}
	
	@Test
	public void test() {
	
	}

}
