package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
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
	
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal D = new Nonterminal("D");
	
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
	public void test() {
		Input input = Input.fromString("xca");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode1()));
		
		input = Input.fromString("ycb");
		parser = ParserFactory.newParser(grammarGraph, input);
		result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode2()));
		
		input = Input.fromString("cababaab");
		parser = ParserFactory.newParser(grammarGraph, input);
		result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode3()));
		
		input = Input.fromString("xcabbbbb");
		parser = ParserFactory.newParser(grammarGraph, input);
		result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode4()));

		input = Input.fromString("ycaaaabaaaa");
		parser = ParserFactory.newParser(grammarGraph, input);
		result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode5()));
	}
	
	private SPPFNode getSPPFNode1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(x), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 2);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(D), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(y), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 2);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 8);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 7);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(D), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 7);
		IntermediateNode node5 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 6);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 6);
		IntermediateNode node8 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 5);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 5);
		IntermediateNode node10 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 4);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 4);
		IntermediateNode node12 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 3);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		IntermediateNode node14 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 2);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 2);
		IntermediateNode node16 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 1);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 1);
		TokenSymbolNode node18 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 0, 1);
		node17.addChild(node18);
		node16.addChild(node6);
		node16.addChild(node17);
		TokenSymbolNode node19 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		node15.addChild(node16);
		node15.addChild(node19);
		node14.addChild(node3);
		node14.addChild(node15);
		TokenSymbolNode node20 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 2, 1);
		node13.addChild(node14);
		node13.addChild(node20);
		node12.addChild(node6);
		node12.addChild(node13);
		TokenSymbolNode node21 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 3, 1);
		node11.addChild(node12);
		node11.addChild(node21);
		node10.addChild(node3);
		node10.addChild(node11);
		TokenSymbolNode node22 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 4, 1);
		node9.addChild(node10);
		node9.addChild(node22);
		node8.addChild(node6);
		node8.addChild(node9);
		TokenSymbolNode node23 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 5, 1);
		node7.addChild(node8);
		node7.addChild(node23);
		node5.addChild(node6);
		node5.addChild(node7);
		TokenSymbolNode node24 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 6, 1);
		node4.addChild(node5);
		node4.addChild(node24);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node25 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 7, 1);
		node1.addChild(node2);
		node1.addChild(node25);
		return node1;
	}
	
	private SPPFNode getSPPFNode4() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 8);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 7);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(D), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 7);
		IntermediateNode node5 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 6);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 6);
		IntermediateNode node7 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 5);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 5);
		IntermediateNode node9 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 4);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 4);
		IntermediateNode node11 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 3);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 3);
		IntermediateNode node13 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 2);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 1);
		TokenSymbolNode node15 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(x), 0, 1);
		node14.addChild(node15);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 2);
		TokenSymbolNode node17 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 1, 1);
		node16.addChild(node17);
		node13.addChild(node14);
		node13.addChild(node16);
		TokenSymbolNode node18 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node12.addChild(node13);
		node12.addChild(node18);
		node11.addChild(node3);
		node11.addChild(node12);
		TokenSymbolNode node19 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 3, 1);
		node10.addChild(node11);
		node10.addChild(node19);
		node9.addChild(node3);
		node9.addChild(node10);
		TokenSymbolNode node20 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 4, 1);
		node8.addChild(node9);
		node8.addChild(node20);
		node7.addChild(node3);
		node7.addChild(node8);
		TokenSymbolNode node21 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 5, 1);
		node6.addChild(node7);
		node6.addChild(node21);
		node5.addChild(node3);
		node5.addChild(node6);
		TokenSymbolNode node22 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 6, 1);
		node4.addChild(node5);
		node4.addChild(node22);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node23 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 7, 1);
		node1.addChild(node2);
		node1.addChild(node23);
		return node1;
	}
	
	private SPPFNode getSPPFNode5() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 11);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 10);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 10);
		IntermediateNode node5 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 9);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 9);
		IntermediateNode node7 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 8);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 8);
		IntermediateNode node9 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 7);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 7);
		IntermediateNode node11 = new IntermediateNode(grammarGraph.getIntermediateNodeId(D, A), 0, 6);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(D), 2, 0, 1);
		TokenSymbolNode node13 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(y), 0, 1);
		node12.addChild(node13);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 6);
		IntermediateNode node15 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 1, 5);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 1, 1);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 5);
		IntermediateNode node18 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 1, 4);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 4);
		IntermediateNode node20 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 1, 3);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 3);
		IntermediateNode node22 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 1, 2);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 1, 2);
		TokenSymbolNode node24 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 1, 1);
		node23.addChild(node24);
		node22.addChild(node16);
		node22.addChild(node23);
		TokenSymbolNode node25 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node21.addChild(node22);
		node21.addChild(node25);
		node20.addChild(node16);
		node20.addChild(node21);
		TokenSymbolNode node26 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 3, 1);
		node19.addChild(node20);
		node19.addChild(node26);
		node18.addChild(node16);
		node18.addChild(node19);
		TokenSymbolNode node27 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 4, 1);
		node17.addChild(node18);
		node17.addChild(node27);
		node15.addChild(node16);
		node15.addChild(node17);
		TokenSymbolNode node28 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 5, 1);
		node14.addChild(node15);
		node14.addChild(node28);
		node11.addChild(node12);
		node11.addChild(node14);
		TokenSymbolNode node29 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 6, 1);
		node10.addChild(node11);
		node10.addChild(node29);
		node9.addChild(node3);
		node9.addChild(node10);
		TokenSymbolNode node30 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 7, 1);
		node8.addChild(node9);
		node8.addChild(node30);
		node7.addChild(node3);
		node7.addChild(node8);
		TokenSymbolNode node31 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 8, 1);
		node6.addChild(node7);
		node6.addChild(node31);
		node5.addChild(node3);
		node5.addChild(node6);
		TokenSymbolNode node32 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 9, 1);
		node4.addChild(node5);
		node4.addChild(node32);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node33 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 10, 1);
		node1.addChild(node2);
		node1.addChild(node33);
		return node1;
	}
	
}