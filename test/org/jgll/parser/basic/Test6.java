package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test6 {

	private GrammarGraph grammarGraph;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(a, B, c));
		Rule r2 = new Rule(A, list(C));
		Rule r3 = new Rule(B, list(b));
		Rule r4 = new Rule(C, list(a, C));
		Rule r5 = new Rule(C, list(c));
		
		grammarGraph = new Grammar().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("B").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("C").isNullable());
	}
	
	@Test
	public void testLL1() {
		assertTrue(grammarGraph.isLL1SubGrammar(A));
		assertTrue(grammarGraph.isLL1SubGrammar(B));
		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF1()));
	}
	
	@Test
	public void testParser2() {
		Input input = Input.fromString("aaaac");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF2()));
	}
	
	private SPPFNode getSPPF1() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 3);
		IntermediateNode node2 = factory.createIntermediateNode(list(a, B), 0, 2);
		TokenSymbolNode node3 = factory.createTokenNode(a, 0, 1);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(B, 1, 2);
		TokenSymbolNode node5 = factory.createTokenNode(b, 1, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = factory.createTokenNode(c, 2, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 5);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(C, 0, 5);
		TokenSymbolNode node3 = factory.createTokenNode(a, 0, 1);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(C, 1, 5);
		TokenSymbolNode node5 = factory.createTokenNode(a, 1, 1);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(C, 2, 5);
		TokenSymbolNode node7 = factory.createTokenNode(a, 2, 1);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(C, 3, 5);
		TokenSymbolNode node9 = factory.createTokenNode(a, 3, 1);
		NonterminalSymbolNode node10 = factory.createNonterminalNode(C, 4, 5);
		TokenSymbolNode node11 = factory.createTokenNode(c, 4, 1);
		node10.addChild(node11);
		node8.addChild(node9);
		node8.addChild(node10);
		node6.addChild(node7);
		node6.addChild(node8);
		node4.addChild(node5);
		node4.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node4);
		node1.addChild(node2);
		return node1;
	}
	
}
	