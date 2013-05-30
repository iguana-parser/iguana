package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.GrammarToDot;
import org.jgll.util.GraphVizUtil;
import org.jgll.util.Input;
import org.jgll.util.SPPFToDot;
import org.jgll.util.ToDotWithoutIntermeidateAndLists;
import org.junit.Test;

/**
 * 
 * E ::= E E+    (non-assoc)
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
		Nonterminal E = new Nonterminal("E");
		rule1 = new Rule(E, list(E, new Nonterminal("E+", true)));
		builder.addRule(rule1);
		
		// E ::=  E + E
		rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= a
		rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		rule4 = new Rule(new Nonterminal("E+", true), list(new Nonterminal("E+", true), E));
		builder.addRule(rule4);
		
		// E+ ::= E
		rule5 = new Rule(new Nonterminal("E+", true), list(E));
		builder.addRule(rule5);
		
		// (E ::= .E E+, E E+) 
		builder.addFilter(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		builder.addFilter(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		builder.addFilter(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		builder.addFilter(E, rule1, 1, rule2);
		
		// (E ::= E .E, E + E)
		builder.addFilter(E, rule2, 2, rule2);
		
		builder.filter();
		return builder.build();
	}

	@Test
	public void testAssociativityAndPriority() {
		
		GraphVizUtil.generateGraph(GrammarToDot.toDot(grammar), "/Users/ali/output", "grammar", GraphVizUtil.L2R);

		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), "/Users/ali/output", "graph");
	}

}
