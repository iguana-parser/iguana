package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test9 {

	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');

	
	@Before
	public void init() {
		
		Rule r1 = new Rule(S, list(A, A, b));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(A);
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertEquals(1, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node2 = factory.createPackedNode("S ::= A A b .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A A . b", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= A A . b", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("A ::= a .", 0, node5);
		TokenSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 1, 1).init();
		PackedNode node9 = factory.createPackedNode("A ::= .", 1, node8);
		TokenSymbolNode node10 = factory.createTokenNode("epsilon", 1, 0);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		PackedNode node11 = factory.createPackedNode("S ::= A A . b", 0, node3);
		NonterminalNode node12 = factory.createNonterminalNode("A", 0, 0, 0).init();
		PackedNode node13 = factory.createPackedNode("A ::= .", 0, node12);
		TokenSymbolNode node14 = factory.createTokenNode("epsilon", 0, 0);
		node13.addChild(node14);
		node12.addChild(node13);
		node11.addChild(node12);
		node11.addChild(node5);
		node3.addChild(node4);
		node3.addChild(node11);
		TokenSymbolNode node15 = factory.createTokenNode("b", 1, 1);
		node2.addChild(node3);
		node2.addChild(node15);
		node1.addChild(node2);
		return node1;
	}
}
	