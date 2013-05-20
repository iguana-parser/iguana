package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.GrammarToDot;
import org.jgll.util.GraphVizUtil;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * E ::= E E+
 *     > E + E
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest3 extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;
	private Rule rule4;
	private Rule rule5;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E E+
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Nonterminal("E+")));
		builder.addRule(rule1);
		
		// E ::= - E
		rule2 = new Rule(new Nonterminal("E"), list(new Character('+'), new Nonterminal("E")));
		builder.addRule(rule2);
		
		// E ::= a
		rule3 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		rule4 = new Rule(new Nonterminal("E+"), list(new Nonterminal("E+"), new Nonterminal("E")));
		builder.addRule(rule4);
		
		// E+ ::= E
		rule5 = new Rule(new Nonterminal("E+"), list(new Nonterminal("E")));
		builder.addRule(rule5);
		
		// E E+ non-assoc
		builder.addFilter("E", rule1.getBody(), 0, rule1.getBody());
		// E E+ > E + E
		builder.addFilter("E", rule1.getBody(), 0, rule2.getBody());
		// E + E left
		builder.addFilter("E", rule2.getBody(), 2, rule2.getBody());
		// E+ E  non-assoc (inherited from rule1)
		builder.addFilter("E", rule4.getBody(), 1, rule1.getBody());
		// E non-assoc (inherited from rule1)
		builder.addFilter("E", rule5.getBody(), 0, rule1.getBody());
		
		return builder.build();
	}

	@Test
	public void testAssociativityAndPriority() {
		
		GraphVizUtil.generateGraph(GrammarToDot.toDot(grammar), "/Users/ali/output", "grammar", GraphVizUtil.L2R);

		
//		grammar.filter();
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aa"), grammar, "E");
//		generateGraphWithoutIntermeiateNodes(sppf);
		generateGraphWithIntermeiateAndListNodes(sppf);
	}

}
