package org.jgll.grammar;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test1 {
	
	private Grammar grammar;
	private GLLParser parser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("A"));
		grammar = new GrammarBuilder("epsilon").addRule(r1).build();
		recognizer = RecognizerFactory.contextFreeRecognizer(grammar);
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertTrue(grammar.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void testSPPF() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString(""), grammar, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
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
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0);
		return node1;
	}

}