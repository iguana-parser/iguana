package org.jgll.grammar;

import java.util.Arrays;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.GraphVizUtil;
import org.junit.Test;

import static junit.framework.Assert.*;

public class AddGrammarTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		// E ::= E + E | a
		Rule rule1 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("E"), new Character('+'), new Nonterminal("E")).build();
		Rule rule2 = new Rule.Builder().head(new Nonterminal("E")).body(new Character('a')).build();
		return Grammar.fromRules("gamma2", Arrays.asList(rule1, rule2));
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void parse() {
		NonterminalSymbolNode sppf = parser.parse("a+a", grammar, "E");
		GraphVizUtil.generateGraph(sppf, outputDir, "graph");
	}
 
}
