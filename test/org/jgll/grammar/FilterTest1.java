package org.jgll.grammar;

import java.util.Arrays;
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
		// E ::= E + E
		rule1 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("E"), new Character('+'), new Nonterminal("E")).build();
		// E ::= - E
		rule2 = new Rule.Builder().head(new Nonterminal("E")).body(new Character('-'), new Nonterminal("E")).build();
		// E ::= a
		rule3 = new Rule.Builder().head(new Nonterminal("E")).body(new Character('a')).build();
		return Grammar.fromRules("gamma2", Arrays.asList(rule1, rule2, rule3));
	}
	
	
	@Test
	public void testAssociativityAndPriority() {
		Set<Filter> filters = new HashSet<>();
		filters.add(new Filter(rule1, 2, set(rule1)));
		filters.add(new Filter(rule1, 0, set(rule2)));
		grammar.filter(filters);
		System.out.println(grammar);
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+-a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	
}
