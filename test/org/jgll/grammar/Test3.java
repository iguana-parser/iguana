package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
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
import org.jgll.sppf.TokenSymbolNode;
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
	private GLLRecognizer recognizer;
	
	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Nonterminal("B"), new Nonterminal("C")));
		Rule r2 = new Rule(new Nonterminal("B"), list(new Character('b')));
		Rule r3 = new Rule(new Nonterminal("C"), list(new Character('c')));
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		grammar = new GrammarBuilder("test3", factory).addRule(r1).addRule(r2).addRule(r3).build();
		
		recognizer = RecognizerFactory.contextFreeRecognizer(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammar.getHeadGrammarSlot("B").isNullable());
		assertFalse(grammar.getHeadGrammarSlot("C").isNullable());
	}
	
	@Test
	public void testLL1() {
		assertTrue(grammar.getHeadGrammarSlot("A").isLL1SubGrammar());
		assertTrue(grammar.getHeadGrammarSlot("B").isLL1SubGrammar());
		assertTrue(grammar.getHeadGrammarSlot("C").isLL1SubGrammar());
		assertTrue(grammar.getHeadGrammarSlot("A").isLL1SubGrammar());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("bc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
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
		TokenSymbolNode node0 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("B"), 0, 1);
		node1.addChild(node0);
		TokenSymbolNode node2 = new TokenSymbolNode(3, 1, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("C"), 1, 2);
		node3.addChild(node2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("A"), 0, 2);
		node4.addChild(node1);
		node4.addChild(node3);
		return node4;
	}
	
}