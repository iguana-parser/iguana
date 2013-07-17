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
		Rule r1 = new Rule(new Nonterminal("A"), emptyList());
		return new GrammarBuilder("epsilon").addRule(r1).build();
	}
	
	@Test
	public void testParser() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString(""), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testRecognizerSuccess() {
		boolean result = recognizer.recognize(Input.fromString(""), grammar, "A");
		assertEquals(true, result);
	}
	
	@Test
	public void testRecognizerFail() {
		boolean result = recognizer.recognize(Input.fromString("a"), grammar, "A");
		assertEquals(false, result);
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode(-2, 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0);
		node1.addChild(node0);
		return node1;
	}

}