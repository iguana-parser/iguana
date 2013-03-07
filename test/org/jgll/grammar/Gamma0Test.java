package org.jgll.grammar;


import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import org.jgll.parser.GrammarInterpreter;
import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Test;

public class Gamma0Test extends AbstractGrammarTest {

	/**
	 *	S ::= a S | A S d | epsilon
	 * 	A ::= a
	 */
	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("A"), new Nonterminal("S"), new Character('d')).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("S")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("gamma1", asList(r1, r2, r3, r4));
	}
	
	@Test
	public void testNullables() {
		assertEquals(true, grammar.getNonterminalByName("S").isNullable());
		assertEquals(false, grammar.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void testLongestGrammarChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(new Character('a'), Epsilon.getInstance()), grammar.getNonterminalByName("S").getFirstSet());
		assertEquals(set(new Character('a')), grammar.getNonterminalByName("A").getFirstSet());
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(new Character('a'), new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("A").getFollowSet());
		assertEquals(set(new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("S").getFollowSet());
	}
	
	@Test
	public void test() {
		GrammarInterpreter parser = new GrammarInterpreter();
		NonterminalSymbolNode sppf = parser.parse("aad", grammar, "S");
		System.out.println(sppf);
	}
	
	
}
