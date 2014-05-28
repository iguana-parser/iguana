package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A a
 *     | D A b
 *     | c
 * 
 * B ::= x | epsilon
 * 
 * D ::= y | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class HiddenLeftRecursion1Test {

	private GrammarGraph grammarGraph;
	
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal D = Nonterminal.withName("D");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	private Character x = Character.from('x');
	private Character y = Character.from('y');

	@Before
	public void init() {		
		Rule r1 = new Rule(A, list(B, A, a));
		Rule r2 = new Rule(A, list(D, A, b));
		Rule r3 = new Rule(A, list(c));
		Rule r4 = new Rule(B, list(x));
		Rule r5 = new Rule(B);
		Rule r6 = new Rule(D, list(y));
		Rule r7 = new Rule(D);
		
		grammarGraph = new Grammar()
														 .addRule(r1)
														 .addRule(r2)
														 .addRule(r3)
														 .addRule(r4)
														 .addRule(r5)
														 .addRule(r6)
														 .addRule(r7).toGrammarGraph();
			
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("xca");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("ycb");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode2()));
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("cababaab");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode3()));
	}
	
	@Test
	public void test4() {
		Input input = Input.fromString("xcabbbbb");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode4()));
	}
	
	@Test
	public void test5() {
		Input input = Input.fromString("ycaaaabaaaa");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode5()));
	}
	
	private SPPFNode getSPPFNode1() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 3);
		IntermediateNode node2 = factory.createIntermediateNode(list(B, A), 0, 2);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(B, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(x, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = factory.createNonterminalNode(A, 1, 2);
		TokenSymbolNode node6 = factory.createTokenNode(c, 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = factory.createTokenNode(a, 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 3);
		IntermediateNode node2 = factory.createIntermediateNode(list(D, A), 0, 2);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(D, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(y, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = factory.createNonterminalNode(A, 1, 2);
		TokenSymbolNode node6 = factory.createTokenNode(c, 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = factory.createTokenNode(b, 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 8);
		IntermediateNode node2 = factory.createIntermediateNode(list(D, A), 0, 7);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(D, 0, 0);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(A, 0, 7);
		IntermediateNode node5 = factory.createIntermediateNode(list(B, A), 0, 6);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(B, 0, 0);
		NonterminalSymbolNode node7 = factory.createNonterminalNode(A, 0, 6);
		IntermediateNode node8 = factory.createIntermediateNode(list(B, A), 0, 5);
		NonterminalSymbolNode node9 = factory.createNonterminalNode(A, 0, 5);
		IntermediateNode node10 = factory.createIntermediateNode(list(D, A), 0, 4);
		NonterminalSymbolNode node11 = factory.createNonterminalNode(A, 0, 4);
		IntermediateNode node12 = factory.createIntermediateNode(list(B, A), 0, 3);
		NonterminalSymbolNode node13 = factory.createNonterminalNode(A, 0, 3);
		IntermediateNode node14 = factory.createIntermediateNode(list(D, A), 0, 2);
		NonterminalSymbolNode node15 = factory.createNonterminalNode(A, 0, 2);
		IntermediateNode node16 = factory.createIntermediateNode(list(B, A), 0, 1);
		NonterminalSymbolNode node17 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node18 = factory.createTokenNode(c, 0, 1);
		node17.addChild(node18);
		node16.addChild(node6);
		node16.addChild(node17);
		TokenSymbolNode node19 = factory.createTokenNode(a, 1, 1);
		node15.addChild(node16);
		node15.addChild(node19);
		node14.addChild(node3);
		node14.addChild(node15);
		TokenSymbolNode node20 = factory.createTokenNode(b, 2, 1);
		node13.addChild(node14);
		node13.addChild(node20);
		node12.addChild(node6);
		node12.addChild(node13);
		TokenSymbolNode node21 = factory.createTokenNode(a, 3, 1);
		node11.addChild(node12);
		node11.addChild(node21);
		node10.addChild(node3);
		node10.addChild(node11);
		TokenSymbolNode node22 = factory.createTokenNode(b, 4, 1);
		node9.addChild(node10);
		node9.addChild(node22);
		node8.addChild(node6);
		node8.addChild(node9);
		TokenSymbolNode node23 = factory.createTokenNode(a, 5, 1);
		node7.addChild(node8);
		node7.addChild(node23);
		node5.addChild(node6);
		node5.addChild(node7);
		TokenSymbolNode node24 = factory.createTokenNode(a, 6, 1);
		node4.addChild(node5);
		node4.addChild(node24);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node25 = factory.createTokenNode(b, 7, 1);
		node1.addChild(node2);
		node1.addChild(node25);
		return node1;
	}
	
	private SPPFNode getSPPFNode4() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 8);
		IntermediateNode node2 = factory.createIntermediateNode(list(D, A), 0, 7);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(D, 0, 0);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(A, 0, 7);
		IntermediateNode node5 = factory.createIntermediateNode(list(D, A), 0, 6);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(A, 0, 6);
		IntermediateNode node7 = factory.createIntermediateNode(list(D, A), 0, 5);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(A, 0, 5);
		IntermediateNode node9 = factory.createIntermediateNode(list(D, A), 0, 4);
		NonterminalSymbolNode node10 = factory.createNonterminalNode(A, 0, 4);
		IntermediateNode node11 = factory.createIntermediateNode(list(D, A), 0, 3);
		NonterminalSymbolNode node12 = factory.createNonterminalNode(A, 0, 3);
		IntermediateNode node13 = factory.createIntermediateNode(list(B, A), 0, 2);
		NonterminalSymbolNode node14 = factory.createNonterminalNode(B, 0, 1);
		TokenSymbolNode node15 = factory.createTokenNode(x, 0, 1);
		node14.addChild(node15);
		NonterminalSymbolNode node16 = factory.createNonterminalNode(A, 1, 2);
		TokenSymbolNode node17 = factory.createTokenNode(c, 1, 1);
		node16.addChild(node17);
		node13.addChild(node14);
		node13.addChild(node16);
		TokenSymbolNode node18 = factory.createTokenNode(a, 2, 1);
		node12.addChild(node13);
		node12.addChild(node18);
		node11.addChild(node3);
		node11.addChild(node12);
		TokenSymbolNode node19 = factory.createTokenNode(b, 3, 1);
		node10.addChild(node11);
		node10.addChild(node19);
		node9.addChild(node3);
		node9.addChild(node10);
		TokenSymbolNode node20 = factory.createTokenNode(b, 4, 1);
		node8.addChild(node9);
		node8.addChild(node20);
		node7.addChild(node3);
		node7.addChild(node8);
		TokenSymbolNode node21 = factory.createTokenNode(b, 5, 1);
		node6.addChild(node7);
		node6.addChild(node21);
		node5.addChild(node3);
		node5.addChild(node6);
		TokenSymbolNode node22 = factory.createTokenNode(b, 6, 1);
		node4.addChild(node5);
		node4.addChild(node22);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node23 = factory.createTokenNode(b, 7, 1);
		node1.addChild(node2);
		node1.addChild(node23);
		return node1;
	}
	
	private SPPFNode getSPPFNode5() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 11);
		IntermediateNode node2 = factory.createIntermediateNode(list(B, A), 0, 10);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(B, 0, 0);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(A, 0, 10);
		IntermediateNode node5 = factory.createIntermediateNode(list(B, A), 0, 9);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(A, 0, 9);
		IntermediateNode node7 = factory.createIntermediateNode(list(B, A), 0, 8);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(A, 0, 8);
		IntermediateNode node9 = factory.createIntermediateNode(list(B, A), 0, 7);
		NonterminalSymbolNode node10 = factory.createNonterminalNode(A, 0, 7);
		IntermediateNode node11 = factory.createIntermediateNode(list(D, A), 0, 6);
		NonterminalSymbolNode node12 = factory.createNonterminalNode(D, 0, 1);
		TokenSymbolNode node13 = factory.createTokenNode(y, 0, 1);
		node12.addChild(node13);
		NonterminalSymbolNode node14 = factory.createNonterminalNode(A, 1, 6);
		IntermediateNode node15 = factory.createIntermediateNode(list(B, A), 1, 5);
		NonterminalSymbolNode node16 = factory.createNonterminalNode(B, 1, 1);
		NonterminalSymbolNode node17 = factory.createNonterminalNode(A, 1, 5);
		IntermediateNode node18 = factory.createIntermediateNode(list(B, A), 1, 4);
		NonterminalSymbolNode node19 = factory.createNonterminalNode(A, 1, 4);
		IntermediateNode node20 = factory.createIntermediateNode(list(B, A), 1, 3);
		NonterminalSymbolNode node21 = factory.createNonterminalNode(A, 1, 3);
		IntermediateNode node22 = factory.createIntermediateNode(list(B, A), 1, 2);
		NonterminalSymbolNode node23 = factory.createNonterminalNode(A, 1, 2);
		TokenSymbolNode node24 = factory.createTokenNode(c, 1, 1);
		node23.addChild(node24);
		node22.addChild(node16);
		node22.addChild(node23);
		TokenSymbolNode node25 = factory.createTokenNode(a, 2, 1);
		node21.addChild(node22);
		node21.addChild(node25);
		node20.addChild(node16);
		node20.addChild(node21);
		TokenSymbolNode node26 = factory.createTokenNode(a, 3, 1);
		node19.addChild(node20);
		node19.addChild(node26);
		node18.addChild(node16);
		node18.addChild(node19);
		TokenSymbolNode node27 = factory.createTokenNode(a, 4, 1);
		node17.addChild(node18);
		node17.addChild(node27);
		node15.addChild(node16);
		node15.addChild(node17);
		TokenSymbolNode node28 = factory.createTokenNode(a, 5, 1);
		node14.addChild(node15);
		node14.addChild(node28);
		node11.addChild(node12);
		node11.addChild(node14);
		TokenSymbolNode node29 = factory.createTokenNode(b, 6, 1);
		node10.addChild(node11);
		node10.addChild(node29);
		node9.addChild(node3);
		node9.addChild(node10);
		TokenSymbolNode node30 = factory.createTokenNode(a, 7, 1);
		node8.addChild(node9);
		node8.addChild(node30);
		node7.addChild(node3);
		node7.addChild(node8);
		TokenSymbolNode node31 = factory.createTokenNode(a, 8, 1);
		node6.addChild(node7);
		node6.addChild(node31);
		node5.addChild(node3);
		node5.addChild(node6);
		TokenSymbolNode node32 = factory.createTokenNode(a, 9, 1);
		node4.addChild(node5);
		node4.addChild(node32);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node33 = factory.createTokenNode(a, 10, 1);
		node1.addChild(node2);
		node1.addChild(node33);
		return node1;
	}
	
}