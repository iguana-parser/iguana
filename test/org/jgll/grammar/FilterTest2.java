package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * E ::= 0 E ^ E	(right)
 *     > 1 E + E	(left)
 *     > 2 - E
 *     | 3 a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest2 extends AbstractGrammarTest {

	private Rule rule0;
	private Rule rule1;
	private Rule rule2;
	private Rule rule3;


	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		rule0 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('^'), new Nonterminal("E")));
		builder.addRule(rule0);
		
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('+'), new Nonterminal("E")));
		builder.addRule(rule1);
		
		rule2 = new Rule(new Nonterminal("E"), list(new Character('-'), new Nonterminal("E")));
		builder.addRule(rule2);
		
		rule3 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule3);
		return builder.build();
		
	}
	

	@Test
	public void testAssociativityAndPriority() {
		// left associative E + E
		grammar.addFilter("E", rule1.getBody(), 2, rule1.getBody());
		
		// + has higher priority than -
		grammar.addFilter("E", rule1.getBody(), 0, rule2.getBody());
		
		// right associative E ^ E
		grammar.addFilter("E", rule0.getBody(), 0, rule0.getBody());
		
		// ^ has higher priority than -
		grammar.addFilter("E", rule0.getBody(), 0, rule2.getBody());
		
		// ^ has higher priority than +
		grammar.addFilter("E", rule0.getBody(), 0, rule1.getBody());
		grammar.addFilter("E", rule0.getBody(), 2, rule1.getBody());
		
		grammar.filter();
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	

}
