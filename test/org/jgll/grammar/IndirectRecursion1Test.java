package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B C | a
 * 
 * B ::= A | b
 * 
 * C ::= c
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion1Test {

	private Grammar grammar;
	private GrammarBuilder builder;

	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal C = new Nonterminal("C");
	Character a = new Character('a');
	Character b = new Character('b');
	Character c = new Character('c');

	@Before
	public void createGrammar() {
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B, list(A));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		builder = new GrammarBuilder("IndirectRecursion", factory)
													  .addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4)
													  .addRule(r5);
		grammar = builder.build();
	}
	
	
	@Test
	public void testFirstFollowSets() {
		assertEquals(set(a, b), grammar.getFirstSet(A));
		assertEquals(set(a, b), grammar.getFirstSet(B));
		assertEquals(set(c), grammar.getFirstSet(C));
		
		assertEquals(set(c, EOF.getInstance()), grammar.getFollowSet(A));
		assertEquals(set(c, EOF.getInstance()), grammar.getFollowSet(B));
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("bc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testReachabilityGraph() {
		Set<HeadGrammarSlot> set = builder.getDirectReachableNonterminals("A");
		assertTrue(set.contains(grammar.getHeadGrammarSlot("A")));
		assertTrue(set.contains(grammar.getHeadGrammarSlot("B")));
		
		set = builder.getDirectReachableNonterminals("B");
		assertTrue(set.contains(grammar.getHeadGrammarSlot("A")));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node3 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 0, 1);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(C), 1, 1, 2);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(c), 1, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}

}