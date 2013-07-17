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
 * A ::= 'a'
 */
public class Test2 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Character('a')));
		return new GrammarBuilder("a").addRule(r1).build();
	}
	
	@Test
	public void testLevelParser() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testRDParser() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testRecognizerSuccess() {
		boolean result = recognizer.recognize(Input.fromString("a"), grammar, "A");
		assertEquals(true, result);
	}
	
	public void testRecognizerFail1() {
		boolean result = recognizer.recognize(Input.fromString("b"), grammar, "A");
		assertEquals(false, result);
	}
	
	public void testRecognizerFail2() {
		boolean result = recognizer.recognize(Input.fromString("aa"), grammar, "A");
		assertEquals(false, result);
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('a', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 1);
		node1.addChild(node0);
		return node1;
	}

}