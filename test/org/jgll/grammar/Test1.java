package org.jgll.grammar;

import static org.junit.Assert.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * A ::= epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test1 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), epsilon());
		return new GrammarBuilder("epsilon").addRule(r1).build();
	}
	
	@Test
	public void testSPPF() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString(""), grammar, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString(""), grammar, "A");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString(""), grammar, "A");
		assertEquals(sppf1, sppf2);
	}
	
	@Test
	public void testRecognizerSuccess() {
		assertTrue(recognizer.recognize(Input.fromString(""), grammar, "A"));
	}
	
	@Test
	public void testRecognizerFail() {
		assertFalse(recognizer.recognize(Input.fromString("a"), grammar, "A"));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode(-2, 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0);
		node1.addChild(node0);
		return node1;
	}

}