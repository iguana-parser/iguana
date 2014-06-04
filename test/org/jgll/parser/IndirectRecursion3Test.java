package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B c
 *     | C d
 *     | e
 * 
 * B ::= A f
 *     | A g
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion3Test {
	
	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Character c = Character.from('c');
	private Character d = Character.from('d');
	private Character e = Character.from('e');
	private Character f = Character.from('f');
	private Character g = Character.from('g');

	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, c));
		Rule r2 = new Rule(A, list(C, d));
		Rule r3 = new Rule(A, list(e));
		Rule r4 = new Rule(B, list(A, f));
		Rule r5 = new Rule(C, list(A, g));
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3)
								    .addRule(r4).addRule(r5).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
		assertFalse(grammar.isNullable(B));
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("efcfc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("egdgdgd");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(Input.fromString("egdgdgd"), grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode2()));		
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("egdfcgd");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode3()));
	}
	
	private SPPFNode getSPPFNode1() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 5);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(B, 0, 4);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(A, 0, 3);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(B, 0, 2);
		NonterminalSymbolNode node5 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node6 = factory.createTokenNode(e, 0, 1);
		node5.addChild(node6);
		TokenSymbolNode node7 = factory.createTokenNode(f, 1, 1);
		node4.addChild(node5);
		node4.addChild(node7);
		TokenSymbolNode node8 = factory.createTokenNode(c, 2, 1);
		node3.addChild(node4);
		node3.addChild(node8);
		TokenSymbolNode node9 = factory.createTokenNode(f, 3, 1);
		node2.addChild(node3);
		node2.addChild(node9);
		TokenSymbolNode node10 = factory.createTokenNode(c, 4, 1);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 7);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(C, 0, 6);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(A, 0, 5);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(C, 0, 4);
		NonterminalSymbolNode node5 = factory.createNonterminalNode(A, 0, 3);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(C, 0, 2);
		NonterminalSymbolNode node7 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node8 = factory.createTokenNode(e, 0, 1);
		node7.addChild(node8);
		TokenSymbolNode node9 = factory.createTokenNode(g, 1, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = factory.createTokenNode(d, 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		TokenSymbolNode node11 = factory.createTokenNode(g, 3, 1);
		node4.addChild(node5);
		node4.addChild(node11);
		TokenSymbolNode node12 = factory.createTokenNode(d, 4, 1);
		node3.addChild(node4);
		node3.addChild(node12);
		TokenSymbolNode node13 = factory.createTokenNode(g, 5, 1);
		node2.addChild(node3);
		node2.addChild(node13);
		TokenSymbolNode node14 = factory.createTokenNode(d, 6, 1);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 7);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(C, 0, 6);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(A, 0, 5);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(B, 0, 4);
		NonterminalSymbolNode node5 = factory.createNonterminalNode(A, 0, 3);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(C, 0, 2);
		NonterminalSymbolNode node7 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node8 = factory.createTokenNode(e, 0, 1);
		node7.addChild(node8);
		TokenSymbolNode node9 = factory.createTokenNode(g, 1, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = factory.createTokenNode(d, 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		TokenSymbolNode node11 = factory.createTokenNode(f, 3, 1);
		node4.addChild(node5);
		node4.addChild(node11);
		TokenSymbolNode node12 = factory.createTokenNode(c, 4, 1);
		node3.addChild(node4);
		node3.addChild(node12);
		TokenSymbolNode node13 = factory.createTokenNode(g, 5, 1);
		node2.addChild(node3);
		node2.addChild(node13);
		TokenSymbolNode node14 = factory.createTokenNode(d, 6, 1);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
}