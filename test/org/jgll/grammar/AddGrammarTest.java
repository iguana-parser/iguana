package org.jgll.grammar;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Test;

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
	public void testParsers() {
		NonterminalSymbolNode sppf1 = rdParser.parse("a+a", grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse("a+a", grammar, "E");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}
	
	public void testLefAssociativeFilter() {
	}
 
}
