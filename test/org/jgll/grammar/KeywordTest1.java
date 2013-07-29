package org.jgll.grammar;

import static org.junit.Assert.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * A ::= "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest1 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), new Keyword("if"));
		return new GrammarBuilder().addRule(r1).build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(TerminalFactory.from("if")), grammar.getNonterminalByName("A").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(2, grammar.getLongestTerminalChain());
	}

	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("if"), grammar, "A");
		generateSPPFGraph(sppf);
	}
	
}
