package org.jgll.grammar;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

public class ArithmeticExpressionsTest extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		// E ::= E + E
		rule1 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("E"), new Character('+'), new Nonterminal("E")).build();
		// E ::= E * E
		rule2 = new Rule.Builder().head(new Nonterminal("E")).body(new Nonterminal("E"), new Character('*'), new Nonterminal("E")).build();
		// E ::= a
		rule3 = new Rule.Builder().head(new Nonterminal("E")).body(new Character('a')).build();
		return Grammar.fromRules("gamma2", Arrays.asList(rule1, rule2, rule3));
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testParsers() {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a"), grammar, "E");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testLefAssociativeFilter() {
		grammar.filter(rule1, 2, set(rule1));
		grammar.filter(rule2, 2, set(rule2));
		System.out.println(grammar);
	}
	
	@Test
	public void testPriority() {
		grammar.filter(rule2, 0, set(rule1));
		grammar.filter(rule2, 2, set(rule1));
		System.out.println(grammar);
	}
	
	@Test
	public void testAssociativityAndPriority() {
		grammar.filter(rule1, 2, set(rule1));
		grammar.filter(rule2, 0, set(rule1));
		grammar.filter(rule2, 2, set(rule1, rule2));
		System.out.println(grammar);
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	
}
