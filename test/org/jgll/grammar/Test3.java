package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.CollectionsUtil.*;

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
 * A ::= B C
 * B ::= 'b'
 * C ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test3 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;
	
	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Nonterminal("B"), new Nonterminal("C")));
		Rule r2 = new Rule(new Nonterminal("B"), list(new Character('b')));
		Rule r3 = new Rule(new Nonterminal("C"), list(new Character('c')));
		grammar = new GrammarBuilder("test3").addRule(r1).addRule(r2).addRule(r3).build();
		
		rdParser = ParserFactory.levelParser(grammar);
		levelParser = ParserFactory.recursiveDescentParser(grammar);
		recognizer = RecognizerFactory.contextFreeRecognizer(grammar);
	}
	
	@Test
	public void testRDParser() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bc"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testLevelParser() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("bc"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testRecognizerSuccess() {
		boolean result = recognizer.recognize(Input.fromString("bc"), grammar, "A");
		assertEquals(true, result);
	}
	
	@Test
	public void testRecognizerFail1() {
		boolean result = recognizer.recognize(Input.fromString("abc"), grammar, "A");
		assertEquals(false, result);
	}
	
	@Test
	public void testRecognizerFail2() {
		boolean result = recognizer.recognize(Input.fromString("b"), grammar, "A");
		assertEquals(false, result);
	}
	
	@Test
	public void testRecognizerFail3() {
		boolean result = recognizer.recognize(Input.fromString("bca"), grammar, "A");
		assertEquals(false, result);
	}
	
	@Test
	public void testPrefixRecognizer() {
		recognizer = RecognizerFactory.prefixContextFreeRecognizer(grammar);
		boolean result = recognizer.recognize(Input.fromString("bca"), grammar, "A");
		assertEquals(true, result);
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('b', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		node1.addChild(node0);
		TerminalSymbolNode node2 = new TerminalSymbolNode('c', 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 2);
		node3.addChild(node2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		node4.addChild(node1);
		node4.addChild(node3);
		return node4;
	}
	
}