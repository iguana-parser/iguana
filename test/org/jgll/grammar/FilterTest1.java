package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

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
		Set<Filter> filters = new HashSet<>();
		filters.add(new Filter(rule1, 2, set(0)));
		filters.add(new Filter(rule1, 0, set(1)));
		grammar.filter(filters);
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	

}
