package org.jgll.grammar;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class LeftFactorizedArithmeticGrammarTest extends AbstractGrammarTest {
	
	@Override
	protected Grammar initGrammar() {
		/**
		 * 	E  ::= T E1
		 * 	E1 ::= + T E1 | epsilon
		 *  T  ::= F T1
		 *  T1 ::= * F T1 |  epsilon
		 *  F  ::= (E) | a
		 */
		Rule r1 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("T"), new Nonterminal("E1")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("E1")).body(new Character('+'), new Nonterminal("T"), new Nonterminal("E1")).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("E1")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("T")).body(new Nonterminal("F"), new Nonterminal("T1")).build();
		Rule r5 = new Rule.Builder().head(new Nonterminal("T1")).body(new Character('*'), new Nonterminal("F"), new Nonterminal("T1")).build();
		Rule r6 = new Rule.Builder().head(new Nonterminal("T1")).build();
		Rule r7 = new Rule.Builder().head(new Nonterminal("F")).body(new Character('('), new Nonterminal("E"), new Character(')')).build();
		Rule r8 = new Rule.Builder().head(new Nonterminal("F")).body(new Character('a')).build();
		return Grammar.fromRules("LeftFactorizedArithmeticExpressions", asList(r1, r2, r3, r4, r5, r6, r7, r8));
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("E").getFirstSet());
		assertEquals(set(new Character('+'), Epsilon.getInstance()), grammar.getNonterminalByName("E1").getFirstSet());
		assertEquals(set(new Character('*'), Epsilon.getInstance()), grammar.getNonterminalByName("T1").getFirstSet());
		assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("T").getFirstSet());
		assertEquals(set(new Character('('), new Character('a')), grammar.getNonterminalByName("F").getFirstSet());
	}
	
	public void testFollowSets() {
		assertEquals(set(new Character(')'), EOF.getInstance()), grammar.getNonterminalByName("E").getFollowSet());
		assertEquals(set(new Character(')'), EOF.getInstance()), grammar.getNonterminalByName("E1").getFollowSet());
		assertEquals(set(new Character('+'), new Character(')'), EOF.getInstance()), grammar.getNonterminalByName("T1").getFollowSet());
		assertEquals(set(new Character('+'), new Character(')'), EOF.getInstance()), grammar.getNonterminalByName("T").getFollowSet());
		assertEquals(set(new Character('+'), new Character('*'), new Character(')'), EOF.getInstance()), grammar.getNonterminalByName("F").getFollowSet());
	}

}
