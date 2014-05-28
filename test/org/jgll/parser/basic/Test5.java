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
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
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
public class Test5 {

	private GrammarGraph grammarGraph;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(B, list(b));
		Rule r3 = new Rule(C, list(c));
		
		grammarGraph = new Grammar().addRule(r1).addRule(r2).addRule(r3).toGrammarGraph();
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
	public void testParser() {
		Input input = Input.fromString("bc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 2);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(B, 0, 1);
		TokenSymbolNode node3 = factory.createTokenNode(b, 0, 1);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(C, 1, 2);
		TokenSymbolNode node5 = factory.createTokenNode(c, 1, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}
	
}