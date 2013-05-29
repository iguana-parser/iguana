package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.GrammarToDot;
import org.jgll.util.GraphVizUtil;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * E ::= E z   1
 *     > x E   2
 *     > E w   3
 *     > y E   4
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest5 extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;
	private Rule rule4;
	private Rule rule5;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E z
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('z')));
		builder.addRule(rule1);
		
		// E ::=  x E
		rule2 = new Rule(new Nonterminal("E"), list(new Character('x'), new Nonterminal("E")));
		builder.addRule(rule2);
		
		// E ::= E w
		rule3 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('w')));
		builder.addRule(rule3);
		
		// E ::= y E
		rule4 = new Rule(new Nonterminal("E"), list(new Character('y'), new Nonterminal("E")));
		builder.addRule(rule4);
		
		// E ::= a
		rule5 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule5);
		
		// (E, .E z, x E) 
		builder.addFilter("E", rule1.getBody(), 0, rule2.getBody());
		
		// (E, .E z, y E) 
		builder.addFilter("E", rule1.getBody(), 0, rule4.getBody());
		
		// (E, x .E, E w)
		builder.addFilter("E", rule2.getBody(), 1, rule3.getBody());
		
		// (E, .E w, y E)
		builder.addFilter("E", rule3.getBody(), 0, rule4.getBody());
		
		builder.filter();
		return builder.build();
	}

	@Test
	public void testAssociativityAndPriority() {
		GraphVizUtil.generateGraph(GrammarToDot.toDot(grammar), "/Users/ali/output", "grammar", GraphVizUtil.L2R);

		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("xawz"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}

}
