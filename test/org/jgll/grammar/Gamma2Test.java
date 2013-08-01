package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma2Test extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		Rule rule1 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(new Nonterminal("S"), list(new Character('b')));
		builder.addRule(rule3);
		
		return builder.build();
	}
	
	@Test
	public void testLongestTerminalChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
		
	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("bbb"), grammar, "S");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("bbb"), grammar, "S");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void test100bs() throws ParseError {
		Input input = Input.fromString(get100b());
		levelParser.parse(input, grammar, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}

}
