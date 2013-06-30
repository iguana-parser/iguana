package org.jgll.grammar;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.dot.GrammarToDot;
import org.jgll.util.dot.GraphVizUtil;
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
		Nonterminal E = new Nonterminal("E");
		rule1 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule1);
		
		// E ::= - E
		rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);
		
		// E ::= a
		rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		builder.addFilter(E, rule1, 2, rule1);
		builder.addFilter(E, rule1, 0, rule2);
		
		builder.filter();

		return builder.build();
	}

	@Test
	public void testAssociativityAndPriority() throws ParseError {
		System.out.println(grammar);
		GraphVizUtil.generateGraph(GrammarToDot.toDot(grammar), "/Users/ali/output", "grammar", GraphVizUtil.L2R);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}

}
