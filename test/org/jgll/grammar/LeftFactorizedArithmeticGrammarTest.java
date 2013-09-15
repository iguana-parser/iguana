package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class LeftFactorizedArithmeticGrammarTest {
	
	private Grammar grammar;

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("LeftFactorizedArithmeticExpressions");
		
		Rule r1 = new Rule(new Nonterminal("E"), list(new Nonterminal("T"), new Nonterminal("E1")));
		Rule r2 = new Rule(new Nonterminal("E1"), list(new Character('+'), new Nonterminal("T"), new Nonterminal("E1")));
		Rule r3 = new Rule(new Nonterminal("E1"));
		Rule r4 = new Rule(new Nonterminal("T"), list(new Nonterminal("F"), new Nonterminal("T1")));
		Rule r5 = new Rule(new Nonterminal("T1"), list(new Character('*'), new Nonterminal("F"), new Nonterminal("T1")));
		Rule r6 = new Rule(new Nonterminal("T1"));
		Rule r7 = new Rule(new Nonterminal("F"), list(new Character('('), new Nonterminal("E"), new Character(')')));
		Rule r8 = new Rule(new Nonterminal("F"), list(new Character('a')));
		
		
		builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8);
		grammar = builder.build();
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
