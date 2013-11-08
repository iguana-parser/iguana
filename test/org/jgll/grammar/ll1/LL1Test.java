package org.jgll.grammar.ll1;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.BitSetUtil;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A a
 * 
 * A ::= B D
 * 
 * B ::= b | epsilon
 * 
 * D ::= d | epsilon
 * 
 * @author Ali Afroozeh
 * 
 */
public class LL1Test {

	private Grammar grammar;
	private GLLParser rdParser;

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("DanglingElse");

		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal D = new Nonterminal("D");

		Terminal a = new Character('a');
		Terminal b = new Character('b');
		Terminal d = new Character('d');

		Rule rule1 = new Rule(S, list(A, a));
		builder.addRule(rule1);

		Rule rule2 = new Rule(A, list(B, D));
		builder.addRule(rule2);

		Rule rule3 = new Rule(B, list(b));
		builder.addRule(rule3);

		Rule rule4 = new Rule(B);
		builder.addRule(rule4);

		Rule rule5 = new Rule(D, list(d));
		builder.addRule(rule5);

		Rule rule6 = new Rule(D);
		builder.addRule(rule6);

		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("S").isNullable());
		assertTrue(grammar.getNonterminalByName("A").isNullable());
		assertTrue(grammar.getNonterminalByName("B").isNullable());
		assertTrue(grammar.getNonterminalByName("D").isNullable());
	}
	
	@Test
	public void testDirectNullable() {
		assertFalse(grammar.getNonterminalByName("A").isDirectNullable());
		assertTrue(grammar.getNonterminalByName("B").isDirectNullable());
		assertTrue(grammar.getNonterminalByName("D").isDirectNullable());
	}
	
	@Test
	public void ll1Test() {
		assertTrue(grammar.getNonterminalByName("A").isLL1());
		assertTrue(grammar.getNonterminalByName("B").isLL1());
		assertTrue(grammar.getNonterminalByName("D").isLL1());
	}

	@Test
	public void testPredictSets() {
		BodyGrammarSlot slot1 = grammar.getGrammarSlotByName("S ::= . A [a]");
		assertEquals(BitSetUtil.from('d', 'b', 'a'), slot1.getPredictionSet());
		
		BodyGrammarSlot slot2 = grammar.getGrammarSlotByName("A ::= . B D");
		assertEquals(BitSetUtil.from('d', 'b', 'a', 0), slot2.getPredictionSet());
		
		BodyGrammarSlot slot3 = grammar.getGrammarSlotByName("B ::= . [b]");
		assertEquals(BitSetUtil.from('b'), slot3.getPredictionSet());

		BodyGrammarSlot slot4 = grammar.getGrammarSlotByName("B ::= .");
		assertEquals(BitSetUtil.from('d', 'a', 0), slot4.getPredictionSet());
	}

	@Test
	public void test1() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bda"), grammar, "S");
		Visualization.generateSPPFGraph("/Users/ali/output", sppf, Input.fromString("bda"));
	}

}
