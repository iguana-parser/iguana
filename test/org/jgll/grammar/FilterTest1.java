package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * E ::= E + E
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest1 extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E + E
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('+'), new Nonterminal("E")));
		builder.addRule(rule1);
		
		// E ::= - E
		rule2 = new Rule(new Nonterminal("E"), list(new Character('-'), new Nonterminal("E")));
		builder.addRule(rule2);
		
		// E ::= a
		rule3 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule3);
		return builder.build();
	}
	

	@Test
	public void testAssociativityAndPriority() {
		grammar.addFilter("E", 0, 2, set(0));
		grammar.addFilter("E", 0, 0, set(1));
		grammar.filter();
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+-a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	

}
