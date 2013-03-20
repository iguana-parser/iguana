package org.jgll.grammar;

import java.util.Arrays;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class Gamma2Test extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		// S ::= S S S | S S | b
		Rule rule1 = new Rule.Builder().head(new Nonterminal("S"))
									   .body(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")).build();
		Rule rule2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("S"), new Nonterminal("S")).build();
		Rule rule3 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('b')).build();
		return Grammar.fromRules("gamma2", Arrays.asList(rule1, rule2, rule3));
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
		
	@Test
	public void parse() {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("bbb"), grammar, "S");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("bbb"), grammar, "S");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}

}
