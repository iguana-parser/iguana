package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
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

	@Before
	public void createGrammar() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(B, list(A));
		Rule r4 = new Rule(B, list(new Character('b')));
		Rule r5 = new Rule(C, list(new Character('c')));
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		builder = new GrammarBuilder("IndirectRecursion", factory)
													  .addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4)
													  .addRule(r5);
		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
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
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("A"), 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("B"), 0, 1);
		TokenSymbolNode node3 = new TokenSymbolNode(3, 0, 1);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("C"), 1, 2);
		TokenSymbolNode node5 = new TokenSymbolNode(4, 1, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}

}