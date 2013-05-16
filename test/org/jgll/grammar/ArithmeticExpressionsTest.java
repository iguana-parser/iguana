package org.jgll.grammar;

import static junit.framework.Assert.assertEquals;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

public class ArithmeticExpressionsTest extends AbstractGrammarTest {

	private Rule rule0;
	private Rule rule1;
	private Rule rule2;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		// E ::= E + E
		rule0 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('+'), new Nonterminal("E")));
		builder.addRule(rule0);
		
		// E ::= E * E
		rule1 = new Rule(new Nonterminal("E"), list(new Nonterminal("E"), new Character('*'), new Nonterminal("E")));
		builder.addRule(rule1);
		
		// E ::= a
		rule2 = new Rule(new Nonterminal("E"), list(new Character('a')));
		builder.addRule(rule2);
		
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
		grammar.addFilter("E", rule0.getBody(), 2, rule0.getBody());
		grammar.addFilter("E", rule1.getBody(), 2, rule1.getBody());
		grammar.filter();
		System.out.println(grammar);
	}
	
	@Test
	public void testPriority() {
		grammar.addFilter("E", rule1.getBody(), 0, rule0.getBody());
		grammar.addFilter("E", rule1.getBody(), 2, rule0.getBody());
		grammar.filter();
		System.out.println(grammar);
	}
	
	@Test
	public void testAssociativityAndPriority() {
		grammar.addFilter("E", rule0.getBody(), 2, rule0.getBody());
		grammar.addFilter("E", rule1.getBody(), 0, rule0.getBody());
		grammar.addFilter("E", rule1.getBody(), 2, rule0.getBody());
		grammar.addFilter("E", rule1.getBody(), 2, rule1.getBody());
		grammar.filter();
		System.out.println(grammar);
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+a+a"), grammar, "E");
		generateGraphWithoutIntermeiateNodes(sppf);
	}
	
}
