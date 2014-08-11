package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
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
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
public class Gamma2Test {

	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Character b = Character.from('b');
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = new Rule(S, list(S, S, S));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(S, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(b));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParsers1() {
		Input input = Input.fromString("bbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF1()));
	}
	
	@Test
	public void testParsers2() {
		Input input = Input.fromString("bbbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF2()));
	}
	
	@Test
	public void testParsers3() {
		Input input = Input.fromString("bbbbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF3()));
	}	
	
	@Test
	public void test100bs() {
		for (int i = 1; i < 50; i++) {
			Input input = Input.fromString(getBs(i*10));		
			GLLParser parser = ParserFactory.newParser(grammar, input);
			ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
			System.out.println(result.asParseSuccess().getParseStatistics());
		}
	}
	
	private String getBs(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF1() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(S, S), 0, 2);
		NonterminalNode node4 = factory.createNonterminalNode(S, 0, 1);
		TokenSymbolNode node5 = factory.createTokenNode(b, 0, 1);
		node4.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(S, 1, 2);
		TokenSymbolNode node7 = factory.createTokenNode(b, 1, 1);
		node6.addChild(node7);
		node3.addChild(node4);
		node3.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode(S, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(b, 2, 1);
		node8.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node8);
		PackedNode node10 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node1);
		NonterminalNode node11 = factory.createNonterminalNode(S, 1, 3);
		node11.addChild(node6);
		node11.addChild(node8);
		node10.addChild(node4);
		node10.addChild(node11);
		PackedNode node12 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node1);
		NonterminalNode node13 = factory.createNonterminalNode(S, 0, 2);
		node13.addChild(node4);
		node13.addChild(node6);
		node12.addChild(node13);
		node12.addChild(node8);
		node1.addChild(node2);
		node1.addChild(node10);
		node1.addChild(node12);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 4);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(S, S), 0, 3);
		PackedNode node4 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode(S, 0, 1);
		TokenSymbolNode node6 = factory.createTokenNode(b, 0, 1);
		node5.addChild(node6);
		NonterminalNode node7 = factory.createNonterminalNode(S, 1, 3);
		NonterminalNode node8 = factory.createNonterminalNode(S, 1, 2);
		TokenSymbolNode node9 = factory.createTokenNode(b, 1, 1);
		node8.addChild(node9);
		NonterminalNode node10 = factory.createNonterminalNode(S, 2, 3);
		TokenSymbolNode node11 = factory.createTokenNode(b, 2, 1);
		node10.addChild(node11);
		node7.addChild(node8);
		node7.addChild(node10);
		node4.addChild(node5);
		node4.addChild(node7);
		PackedNode node12 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 2, node3);
		NonterminalNode node13 = factory.createNonterminalNode(S, 0, 2);
		node13.addChild(node5);
		node13.addChild(node8);
		node12.addChild(node13);
		node12.addChild(node10);
		node3.addChild(node4);
		node3.addChild(node12);
		NonterminalNode node14 = factory.createNonterminalNode(S, 3, 4);
		TokenSymbolNode node15 = factory.createTokenNode(b, 3, 1);
		node14.addChild(node15);
		node2.addChild(node3);
		node2.addChild(node14);
		PackedNode node16 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node1);
		IntermediateNode node17 = factory.createIntermediateNode(list(S, S), 0, 2);
		node17.addChild(node5);
		node17.addChild(node8);
		NonterminalNode node18 = factory.createNonterminalNode(S, 2, 4);
		node18.addChild(node10);
		node18.addChild(node14);
		node16.addChild(node17);
		node16.addChild(node18);
		PackedNode node19 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node1);
		NonterminalNode node20 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node21 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node20);
		node21.addChild(node17);
		node21.addChild(node10);
		PackedNode node22 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node20);
		node22.addChild(node5);
		node22.addChild(node7);
		PackedNode node23 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node20);
		node23.addChild(node13);
		node23.addChild(node10);
		node20.addChild(node21);
		node20.addChild(node22);
		node20.addChild(node23);
		node19.addChild(node20);
		node19.addChild(node14);
		PackedNode node24 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node1);
		NonterminalNode node25 = factory.createNonterminalNode(S, 1, 4);
		PackedNode node26 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node25);
		IntermediateNode node27 = factory.createIntermediateNode(list(S, S), 1, 3);
		node27.addChild(node8);
		node27.addChild(node10);
		node26.addChild(node27);
		node26.addChild(node14);
		PackedNode node28 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node25);
		node28.addChild(node8);
		node28.addChild(node18);
		PackedNode node29 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node25);
		node29.addChild(node7);
		node29.addChild(node14);
		node25.addChild(node26);
		node25.addChild(node28);
		node25.addChild(node29);
		node24.addChild(node5);
		node24.addChild(node25);
		PackedNode node30 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node1);
		node30.addChild(node13);
		node30.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node16);
		node1.addChild(node19);
		node1.addChild(node24);
		node1.addChild(node30);
		return node1;
	}
	
	private SPPFNode getSPPF3() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 5);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 4, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(S, S), 0, 4);
		PackedNode node4 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode(S, 0, 1);
		TokenSymbolNode node6 = factory.createTokenNode(b, 0, 1);
		node5.addChild(node6);
		NonterminalNode node7 = factory.createNonterminalNode(S, 1, 4);
		PackedNode node8 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node7);
		IntermediateNode node9 = factory.createIntermediateNode(list(S, S), 1, 3);
		NonterminalNode node10 = factory.createNonterminalNode(S, 1, 2);
		TokenSymbolNode node11 = factory.createTokenNode(b, 1, 1);
		node10.addChild(node11);
		NonterminalNode node12 = factory.createNonterminalNode(S, 2, 3);
		TokenSymbolNode node13 = factory.createTokenNode(b, 2, 1);
		node12.addChild(node13);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalNode node14 = factory.createNonterminalNode(S, 3, 4);
		TokenSymbolNode node15 = factory.createTokenNode(b, 3, 1);
		node14.addChild(node15);
		node8.addChild(node9);
		node8.addChild(node14);
		PackedNode node16 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node7);
		NonterminalNode node17 = factory.createNonterminalNode(S, 2, 4);
		node17.addChild(node12);
		node17.addChild(node14);
		node16.addChild(node10);
		node16.addChild(node17);
		PackedNode node18 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node7);
		NonterminalNode node19 = factory.createNonterminalNode(S, 1, 3);
		node19.addChild(node10);
		node19.addChild(node12);
		node18.addChild(node19);
		node18.addChild(node14);
		node7.addChild(node8);
		node7.addChild(node16);
		node7.addChild(node18);
		node4.addChild(node5);
		node4.addChild(node7);
		PackedNode node20 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 3, node3);
		NonterminalNode node21 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node22 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node21);
		IntermediateNode node23 = factory.createIntermediateNode(list(S, S), 0, 2);
		node23.addChild(node5);
		node23.addChild(node10);
		node22.addChild(node23);
		node22.addChild(node12);
		PackedNode node24 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node21);
		node24.addChild(node5);
		node24.addChild(node19);
		PackedNode node25 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node21);
		NonterminalNode node26 = factory.createNonterminalNode(S, 0, 2);
		node26.addChild(node5);
		node26.addChild(node10);
		node25.addChild(node26);
		node25.addChild(node12);
		node21.addChild(node22);
		node21.addChild(node24);
		node21.addChild(node25);
		node20.addChild(node21);
		node20.addChild(node14);
		PackedNode node27 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 2, node3);
		node27.addChild(node26);
		node27.addChild(node17);
		node3.addChild(node4);
		node3.addChild(node20);
		node3.addChild(node27);
		NonterminalNode node28 = factory.createNonterminalNode(S, 4, 5);
		TokenSymbolNode node29 = factory.createTokenNode(b, 4, 1);
		node28.addChild(node29);
		node2.addChild(node3);
		node2.addChild(node28);
		PackedNode node30 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node1);
		IntermediateNode node31 = factory.createIntermediateNode(list(S, S), 0, 3);
		PackedNode node32 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 1, node31);
		node32.addChild(node5);
		node32.addChild(node19);
		PackedNode node33 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 2, node31);
		node33.addChild(node26);
		node33.addChild(node12);
		node31.addChild(node32);
		node31.addChild(node33);
		NonterminalNode node34 = factory.createNonterminalNode(S, 3, 5);
		node34.addChild(node14);
		node34.addChild(node28);
		node30.addChild(node31);
		node30.addChild(node34);
		PackedNode node35 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node1);
		NonterminalNode node36 = factory.createNonterminalNode(S, 2, 5);
		PackedNode node37 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node36);
		node37.addChild(node12);
		node37.addChild(node34);
		PackedNode node38 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 4, node36);
		node38.addChild(node17);
		node38.addChild(node28);
		PackedNode node39 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 4, node36);
		IntermediateNode node40 = factory.createIntermediateNode(list(S, S), 2, 4);
		node40.addChild(node12);
		node40.addChild(node14);
		node39.addChild(node40);
		node39.addChild(node28);
		node36.addChild(node37);
		node36.addChild(node38);
		node36.addChild(node39);
		node35.addChild(node23);
		node35.addChild(node36);
		PackedNode node41 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 4, node1);
		NonterminalNode node42 = factory.createNonterminalNode(S, 0, 4);
		PackedNode node43 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node42);
		node43.addChild(node31);
		node43.addChild(node14);
		PackedNode node44 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 2, node42);
		node44.addChild(node23);
		node44.addChild(node17);
		PackedNode node45 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node42);
		node45.addChild(node21);
		node45.addChild(node14);
		PackedNode node46 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node42);
		node46.addChild(node5);
		node46.addChild(node7);
		PackedNode node47 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node42);
		node47.addChild(node26);
		node47.addChild(node17);
		node42.addChild(node43);
		node42.addChild(node44);
		node42.addChild(node45);
		node42.addChild(node46);
		node42.addChild(node47);
		node41.addChild(node42);
		node41.addChild(node28);
		PackedNode node48 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node1);
		node48.addChild(node21);
		node48.addChild(node34);
		PackedNode node49 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 1, node1);
		NonterminalNode node50 = factory.createNonterminalNode(S, 1, 5);
		PackedNode node51 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 4, node50);
		node51.addChild(node7);
		node51.addChild(node28);
		PackedNode node52 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 3, node50);
		node52.addChild(node9);
		node52.addChild(node34);
		PackedNode node53 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S, S), 4, node50);
		IntermediateNode node54 = factory.createIntermediateNode(list(S, S), 1, 4);
		PackedNode node55 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 2, node54);
		node55.addChild(node10);
		node55.addChild(node17);
		PackedNode node56 = new PackedNode(grammarGraph.getIntermediateNodeId(list(S, S)), 3, node54);
		node56.addChild(node19);
		node56.addChild(node14);
		node54.addChild(node55);
		node54.addChild(node56);
		node53.addChild(node54);
		node53.addChild(node28);
		PackedNode node57 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node50);
		node57.addChild(node10);
		node57.addChild(node36);
		PackedNode node58 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 3, node50);
		node58.addChild(node19);
		node58.addChild(node34);
		node50.addChild(node51);
		node50.addChild(node52);
		node50.addChild(node53);
		node50.addChild(node57);
		node50.addChild(node58);
		node49.addChild(node5);
		node49.addChild(node50);
		PackedNode node59 = new PackedNode(grammarGraph.getPackedNodeId(S, S, S), 2, node1);
		node59.addChild(node26);
		node59.addChild(node36);
		node1.addChild(node2);
		node1.addChild(node30);
		node1.addChild(node35);
		node1.addChild(node41);
		node1.addChild(node48);
		node1.addChild(node49);
		node1.addChild(node59);
		return node1;
	}

}
