package org.jgll.grammar;

import static junit.framework.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

public class ArithmeticExpressionsTest extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		// E ::= E + E
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('+'), new Nonterminal("E")));
		builder.addRule(rule1);
		
		// E ::= E * E
		rule2 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('*'), new Nonterminal("E")));
		builder.addRule(rule2);
		
		// E ::= a
		rule3 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule3);
		
		return builder.build();
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
		Set<Filter> filters = new HashSet<>();
		filters.add(new Filter(rule1, 2, set(0)));
		filters.add(new Filter(rule2, 2, set(1)));
		grammar.filter(filters);
		System.out.println(grammar);
	}
	
	@Test
	public void testPriority() {
		Set<Filter> filters = new HashSet<>();
		filters.add(new Filter(rule2, 0, set(0)));
		filters.add(new Filter(rule2, 2, set(0)));
		grammar.filter(filters);
		System.out.println(grammar);
	}
	
	@Test
	public void testAssociativityAndPriority() {
		Set<Filter> filters = new HashSet<>();
		filters.add(new Filter(rule1, 2, set(0)));
		filters.add(new Filter(rule2, 0, set(0)));
		filters.add(new Filter(rule2, 2, set(0, 1)));
		grammar.filter(filters);
		System.out.println(grammar);
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	
}
