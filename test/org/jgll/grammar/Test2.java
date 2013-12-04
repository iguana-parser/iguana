package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= 'a'
 */
public class Test2 {

	private Grammar grammar;
	private GLLParser parser;
	private GLLRecognizer recognizer;
	
	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Character('a')));
		grammar = new GrammarBuilder("a").addRule(r1).build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
		recognizer = RecognizerFactory.contextFreeRecognizer(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void testRDParser() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("a"), grammar, "A");
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