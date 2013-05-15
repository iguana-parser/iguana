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

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E ^ E
		builder.addRule(new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('^'), new Nonterminal("E"))));
		
		// E ::= E + E
		builder.addRule(new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('+'), new Nonterminal("E"))));
		
		// E ::= - E
		builder.addRule(new Rule(new Nonterminal("E"), list(new Character('-'), new Nonterminal("E"))));
		
		// E ::= a
		builder.addRule(new Rule(new Nonterminal("E"), list(new Character('a'))));
		return builder.build();
	}
	

	@Test
	public void testAssociativityAndPriority() {
		// left associative E + E
		grammar.addFilter("E", 1, 2, 1);
		
		// + has higher priority than -
		grammar.addFilter("E", 1, 0, 2);
		
		// right associative E ^ E
		grammar.addFilter("E", 0, 0, 0);
		
		// ^ has higher priority than -
		grammar.addFilter("E", 0, 0, 2);
		
		// ^ has higher priority than +
		grammar.addFilter("E", 0, 0, 1);
		grammar.addFilter("E", 0, 2, 1);
		
		grammar.filter();
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	

}
