package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
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
	
	private GrammarGraph grammarGraph;
	
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal C = new Nonterminal("C");
	
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
		
		grammarGraph = new Grammar()
									 .addRule(r1)
								     .addRule(r2)
								     .addRule(r3)
								     .addRule(r4)
								     .addRule(r5).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("B").isNullable());
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("efcfc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("egdgdgd");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(Input.fromString("egdgdgd"), grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode2()));		
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("egdfcgd");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode3()));
	}
	
	private SPPFNode getSPPFNode1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 5);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 1, 0, 4);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 1, 0, 2);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 1);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(e), 0, 1);
		node5.addChild(node6);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(f), 1, 1);
		node4.addChild(node5);
		node4.addChild(node7);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 2, 1);
		node3.addChild(node4);
		node3.addChild(node8);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(f), 3, 1);
		node2.addChild(node3);
		node2.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 4, 1);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 7);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 0, 6);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 5);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 0, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 1);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(e), 0, 1);
		node7.addChild(node8);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(g), 1, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(g), 3, 1);
		node4.addChild(node5);
		node4.addChild(node11);
		TokenSymbolNode node12 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 4, 1);
		node3.addChild(node4);
		node3.addChild(node12);
		TokenSymbolNode node13 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(g), 5, 1);
		node2.addChild(node3);
		node2.addChild(node13);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 6, 1);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 7);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 0, 6);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 5);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 1, 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(C), 1, 0, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 1);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(e), 0, 1);
		node7.addChild(node8);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(g), 1, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 2, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(f), 3, 1);
		node4.addChild(node5);
		node4.addChild(node11);
		TokenSymbolNode node12 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 4, 1);
		node3.addChild(node4);
		node3.addChild(node12);
		TokenSymbolNode node13 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(g), 5, 1);
		node2.addChild(node3);
		node2.addChild(node13);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 6, 1);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
}