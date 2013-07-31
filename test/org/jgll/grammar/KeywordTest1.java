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
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		Rule r1 = new Rule(new Nonterminal("A"), iff);
		GrammarBuilder builder = new GrammarBuilder();
		builder.addRule(r1);
		builder.addRule(GrammarBuilder.fromKeyword(iff));
		return builder.build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i')), grammar.getNonterminalByName("A").getFirstSet());
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
