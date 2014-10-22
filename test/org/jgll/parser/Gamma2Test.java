package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
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
		GLLParser parser = ParserFactory.originalParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(31, parseStatistics.getDescriptorsCount());
		assertEquals(6, parseStatistics.getNonterminalNodesCount());
		assertEquals(3, parseStatistics.getIntermediateNodesCount());
		assertEquals(12, parseStatistics.getPackedNodesCount());
		assertEquals(3, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1()));
	}
	
	@Test
	public void testParsers2() {
		Input input = Input.fromString("bbbb");
		GLLParser parser = ParserFactory.originalParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(50, parseStatistics.getDescriptorsCount());
		assertEquals(10, parseStatistics.getNonterminalNodesCount());
		assertEquals(6, parseStatistics.getIntermediateNodesCount());
		assertEquals(28, parseStatistics.getPackedNodesCount());
		assertEquals(4, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF2()));
	}
	
	@Test
	public void testParsers3() {
		Input input = Input.fromString("bbbbb");
		GLLParser parser = ParserFactory.originalParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(74, parseStatistics.getDescriptorsCount());
		assertEquals(15, parseStatistics.getNonterminalNodesCount());
		assertEquals(10, parseStatistics.getIntermediateNodesCount());
		assertEquals(55, parseStatistics.getPackedNodesCount());
		assertEquals(5, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF3()));
	}
	
	@Test
	public void testParsers4() {
		Input input = Input.fromString(getBs(100));
		GLLParser parser = ParserFactory.originalParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(25154, parseStatistics.getDescriptorsCount());
		assertEquals(5050, parseStatistics.getNonterminalNodesCount());
		assertEquals(4950, parseStatistics.getIntermediateNodesCount());
		assertEquals(100, parseStatistics.getTerminalNodesCount());
		assertEquals(495100, parseStatistics.getPackedNodesCount());
	}	
	
	@Test
	public void test() {
		Input input = Input.fromString(getBs(100));
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraphWithoutFirstFollowChecks(), "S");
		System.out.println(BenchmarkUtil.format(result.asParseSuccess().getParseStatistics()));
	}
	
	private String getBs(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF1() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= b .", 0, node3);
		TokenSymbolNode node5 = factory.createTokenNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("S", 1, 3).init();
		PackedNode node7 = factory.createPackedNode("S ::= S S .", 2, node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 1, 2).init();
		PackedNode node9 = factory.createPackedNode("S ::= b .", 1, node8);
		TokenSymbolNode node10 = factory.createTokenNode("b", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		NonterminalNode node11 = factory.createNonterminalNode("S", 2, 3).init();
		PackedNode node12 = factory.createPackedNode("S ::= b .", 2, node11);
		TokenSymbolNode node13 = factory.createTokenNode("b", 2, 1);
		node12.addChild(node13);
		node11.addChild(node12);
		node7.addChild(node8);
		node7.addChild(node11);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		PackedNode node14 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node15 = factory.createNonterminalNode("S", 0, 2).init();
		PackedNode node16 = factory.createPackedNode("S ::= S S .", 1, node15);
		node16.addChild(node3);
		node16.addChild(node8);
		node15.addChild(node16);
		node14.addChild(node15);
		node14.addChild(node11);
		PackedNode node17 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node18 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node19 = factory.createPackedNode("S ::= S S . S", 1, node18);
		node19.addChild(node3);
		node19.addChild(node8);
		node18.addChild(node19);
		node17.addChild(node18);
		node17.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node14);
		node1.addChild(node17);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 4).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= b .", 0, node3);
		TokenSymbolNode node5 = factory.createTokenNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("S", 1, 4).init();
		PackedNode node7 = factory.createPackedNode("S ::= S S S .", 3, node6);
		IntermediateNode node8 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node9 = factory.createPackedNode("S ::= S S . S", 2, node8);
		NonterminalNode node10 = factory.createNonterminalNode("S", 1, 2).init();
		PackedNode node11 = factory.createPackedNode("S ::= b .", 1, node10);
		TokenSymbolNode node12 = factory.createTokenNode("b", 1, 1);
		node11.addChild(node12);
		node10.addChild(node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 2, 3).init();
		PackedNode node14 = factory.createPackedNode("S ::= b .", 2, node13);
		TokenSymbolNode node15 = factory.createTokenNode("b", 2, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		node9.addChild(node10);
		node9.addChild(node13);
		node8.addChild(node9);
		NonterminalNode node16 = factory.createNonterminalNode("S", 3, 4).init();
		PackedNode node17 = factory.createPackedNode("S ::= b .", 3, node16);
		TokenSymbolNode node18 = factory.createTokenNode("b", 3, 1);
		node17.addChild(node18);
		node16.addChild(node17);
		node7.addChild(node8);
		node7.addChild(node16);
		PackedNode node19 = factory.createPackedNode("S ::= S S .", 2, node6);
		NonterminalNode node20 = factory.createNonterminalNode("S", 2, 4).init();
		PackedNode node21 = factory.createPackedNode("S ::= S S .", 3, node20);
		node21.addChild(node13);
		node21.addChild(node16);
		node20.addChild(node21);
		node19.addChild(node10);
		node19.addChild(node20);
		PackedNode node22 = factory.createPackedNode("S ::= S S .", 3, node6);
		NonterminalNode node23 = factory.createNonterminalNode("S", 1, 3).init();
		PackedNode node24 = factory.createPackedNode("S ::= S S .", 2, node23);
		node24.addChild(node10);
		node24.addChild(node13);
		node23.addChild(node24);
		node22.addChild(node23);
		node22.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node19);
		node6.addChild(node22);
		node2.addChild(node3);
		node2.addChild(node6);
		PackedNode node25 = factory.createPackedNode("S ::= S S .", 3, node1);
		NonterminalNode node26 = factory.createNonterminalNode("S", 0, 3).init();
		PackedNode node27 = factory.createPackedNode("S ::= S S .", 1, node26);
		node27.addChild(node3);
		node27.addChild(node23);
		PackedNode node28 = factory.createPackedNode("S ::= S S .", 2, node26);
		NonterminalNode node29 = factory.createNonterminalNode("S", 0, 2).init();
		PackedNode node30 = factory.createPackedNode("S ::= S S .", 1, node29);
		node30.addChild(node3);
		node30.addChild(node10);
		node29.addChild(node30);
		node28.addChild(node29);
		node28.addChild(node13);
		PackedNode node31 = factory.createPackedNode("S ::= S S S .", 2, node26);
		IntermediateNode node32 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node33 = factory.createPackedNode("S ::= S S . S", 1, node32);
		node33.addChild(node3);
		node33.addChild(node10);
		node32.addChild(node33);
		node31.addChild(node32);
		node31.addChild(node13);
		node26.addChild(node27);
		node26.addChild(node28);
		node26.addChild(node31);
		node25.addChild(node26);
		node25.addChild(node16);
		PackedNode node34 = factory.createPackedNode("S ::= S S .", 2, node1);
		node34.addChild(node29);
		node34.addChild(node20);
		PackedNode node35 = factory.createPackedNode("S ::= S S S .", 3, node1);
		IntermediateNode node36 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node37 = factory.createPackedNode("S ::= S S . S", 2, node36);
		node37.addChild(node29);
		node37.addChild(node13);
		PackedNode node38 = factory.createPackedNode("S ::= S S . S", 1, node36);
		node38.addChild(node3);
		node38.addChild(node23);
		node36.addChild(node37);
		node36.addChild(node38);
		node35.addChild(node36);
		node35.addChild(node16);
		PackedNode node39 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node39.addChild(node32);
		node39.addChild(node20);
		node1.addChild(node2);
		node1.addChild(node25);
		node1.addChild(node34);
		node1.addChild(node35);
		node1.addChild(node39);
		return node1;
	}
	
	private SPPFNode getSPPF3() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 5).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= b .", 0, node3);
		TokenSymbolNode node5 = factory.createTokenNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("S", 1, 5).init();
		PackedNode node7 = factory.createPackedNode("S ::= S S .", 4, node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 1, 4).init();
		PackedNode node9 = factory.createPackedNode("S ::= S S S .", 3, node8);
		IntermediateNode node10 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node11 = factory.createPackedNode("S ::= S S . S", 2, node10);
		NonterminalNode node12 = factory.createNonterminalNode("S", 1, 2).init();
		PackedNode node13 = factory.createPackedNode("S ::= b .", 1, node12);
		TokenSymbolNode node14 = factory.createTokenNode("b", 1, 1);
		node13.addChild(node14);
		node12.addChild(node13);
		NonterminalNode node15 = factory.createNonterminalNode("S", 2, 3).init();
		PackedNode node16 = factory.createPackedNode("S ::= b .", 2, node15);
		TokenSymbolNode node17 = factory.createTokenNode("b", 2, 1);
		node16.addChild(node17);
		node15.addChild(node16);
		node11.addChild(node12);
		node11.addChild(node15);
		node10.addChild(node11);
		NonterminalNode node18 = factory.createNonterminalNode("S", 3, 4).init();
		PackedNode node19 = factory.createPackedNode("S ::= b .", 3, node18);
		TokenSymbolNode node20 = factory.createTokenNode("b", 3, 1);
		node19.addChild(node20);
		node18.addChild(node19);
		node9.addChild(node10);
		node9.addChild(node18);
		PackedNode node21 = factory.createPackedNode("S ::= S S .", 2, node8);
		NonterminalNode node22 = factory.createNonterminalNode("S", 2, 4).init();
		PackedNode node23 = factory.createPackedNode("S ::= S S .", 3, node22);
		node23.addChild(node15);
		node23.addChild(node18);
		node22.addChild(node23);
		node21.addChild(node12);
		node21.addChild(node22);
		PackedNode node24 = factory.createPackedNode("S ::= S S .", 3, node8);
		NonterminalNode node25 = factory.createNonterminalNode("S", 1, 3).init();
		PackedNode node26 = factory.createPackedNode("S ::= S S .", 2, node25);
		node26.addChild(node12);
		node26.addChild(node15);
		node25.addChild(node26);
		node24.addChild(node25);
		node24.addChild(node18);
		node8.addChild(node9);
		node8.addChild(node21);
		node8.addChild(node24);
		NonterminalNode node27 = factory.createNonterminalNode("S", 4, 5).init();
		PackedNode node28 = factory.createPackedNode("S ::= b .", 4, node27);
		TokenSymbolNode node29 = factory.createTokenNode("b", 4, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		node7.addChild(node8);
		node7.addChild(node27);
		PackedNode node30 = factory.createPackedNode("S ::= S S S .", 3, node6);
		NonterminalNode node31 = factory.createNonterminalNode("S", 3, 5).init();
		PackedNode node32 = factory.createPackedNode("S ::= S S .", 4, node31);
		node32.addChild(node18);
		node32.addChild(node27);
		node31.addChild(node32);
		node30.addChild(node10);
		node30.addChild(node31);
		PackedNode node33 = factory.createPackedNode("S ::= S S S .", 4, node6);
		IntermediateNode node34 = factory.createIntermediateNode("S ::= S S . S", 1, 4).init();
		PackedNode node35 = factory.createPackedNode("S ::= S S . S", 2, node34);
		node35.addChild(node12);
		node35.addChild(node22);
		PackedNode node36 = factory.createPackedNode("S ::= S S . S", 3, node34);
		node36.addChild(node25);
		node36.addChild(node18);
		node34.addChild(node35);
		node34.addChild(node36);
		node33.addChild(node34);
		node33.addChild(node27);
		PackedNode node37 = factory.createPackedNode("S ::= S S .", 2, node6);
		NonterminalNode node38 = factory.createNonterminalNode("S", 2, 5).init();
		PackedNode node39 = factory.createPackedNode("S ::= S S S .", 4, node38);
		IntermediateNode node40 = factory.createIntermediateNode("S ::= S S . S", 2, 4).init();
		PackedNode node41 = factory.createPackedNode("S ::= S S . S", 3, node40);
		node41.addChild(node15);
		node41.addChild(node18);
		node40.addChild(node41);
		node39.addChild(node40);
		node39.addChild(node27);
		PackedNode node42 = factory.createPackedNode("S ::= S S .", 3, node38);
		node42.addChild(node15);
		node42.addChild(node31);
		PackedNode node43 = factory.createPackedNode("S ::= S S .", 4, node38);
		node43.addChild(node22);
		node43.addChild(node27);
		node38.addChild(node39);
		node38.addChild(node42);
		node38.addChild(node43);
		node37.addChild(node12);
		node37.addChild(node38);
		PackedNode node44 = factory.createPackedNode("S ::= S S .", 3, node6);
		node44.addChild(node25);
		node44.addChild(node31);
		node6.addChild(node7);
		node6.addChild(node30);
		node6.addChild(node33);
		node6.addChild(node37);
		node6.addChild(node44);
		node2.addChild(node3);
		node2.addChild(node6);
		PackedNode node45 = factory.createPackedNode("S ::= S S .", 4, node1);
		NonterminalNode node46 = factory.createNonterminalNode("S", 0, 4).init();
		PackedNode node47 = factory.createPackedNode("S ::= S S .", 1, node46);
		node47.addChild(node3);
		node47.addChild(node8);
		PackedNode node48 = factory.createPackedNode("S ::= S S .", 3, node46);
		NonterminalNode node49 = factory.createNonterminalNode("S", 0, 3).init();
		PackedNode node50 = factory.createPackedNode("S ::= S S .", 1, node49);
		node50.addChild(node3);
		node50.addChild(node25);
		PackedNode node51 = factory.createPackedNode("S ::= S S .", 2, node49);
		NonterminalNode node52 = factory.createNonterminalNode("S", 0, 2).init();
		PackedNode node53 = factory.createPackedNode("S ::= S S .", 1, node52);
		node53.addChild(node3);
		node53.addChild(node12);
		node52.addChild(node53);
		node51.addChild(node52);
		node51.addChild(node15);
		PackedNode node54 = factory.createPackedNode("S ::= S S S .", 2, node49);
		IntermediateNode node55 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node56 = factory.createPackedNode("S ::= S S . S", 1, node55);
		node56.addChild(node3);
		node56.addChild(node12);
		node55.addChild(node56);
		node54.addChild(node55);
		node54.addChild(node15);
		node49.addChild(node50);
		node49.addChild(node51);
		node49.addChild(node54);
		node48.addChild(node49);
		node48.addChild(node18);
		PackedNode node57 = factory.createPackedNode("S ::= S S .", 2, node46);
		node57.addChild(node52);
		node57.addChild(node22);
		PackedNode node58 = factory.createPackedNode("S ::= S S S .", 3, node46);
		IntermediateNode node59 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node60 = factory.createPackedNode("S ::= S S . S", 2, node59);
		node60.addChild(node52);
		node60.addChild(node15);
		PackedNode node61 = factory.createPackedNode("S ::= S S . S", 1, node59);
		node61.addChild(node3);
		node61.addChild(node25);
		node59.addChild(node60);
		node59.addChild(node61);
		node58.addChild(node59);
		node58.addChild(node18);
		PackedNode node62 = factory.createPackedNode("S ::= S S S .", 2, node46);
		node62.addChild(node55);
		node62.addChild(node22);
		node46.addChild(node47);
		node46.addChild(node48);
		node46.addChild(node57);
		node46.addChild(node58);
		node46.addChild(node62);
		node45.addChild(node46);
		node45.addChild(node27);
		PackedNode node63 = factory.createPackedNode("S ::= S S .", 3, node1);
		node63.addChild(node49);
		node63.addChild(node31);
		PackedNode node64 = factory.createPackedNode("S ::= S S S .", 4, node1);
		IntermediateNode node65 = factory.createIntermediateNode("S ::= S S . S", 0, 4).init();
		PackedNode node66 = factory.createPackedNode("S ::= S S . S", 3, node65);
		node66.addChild(node49);
		node66.addChild(node18);
		PackedNode node67 = factory.createPackedNode("S ::= S S . S", 2, node65);
		node67.addChild(node52);
		node67.addChild(node22);
		PackedNode node68 = factory.createPackedNode("S ::= S S . S", 1, node65);
		node68.addChild(node3);
		node68.addChild(node8);
		node65.addChild(node66);
		node65.addChild(node67);
		node65.addChild(node68);
		node64.addChild(node65);
		node64.addChild(node27);
		PackedNode node69 = factory.createPackedNode("S ::= S S .", 2, node1);
		node69.addChild(node52);
		node69.addChild(node38);
		PackedNode node70 = factory.createPackedNode("S ::= S S S .", 3, node1);
		node70.addChild(node59);
		node70.addChild(node31);
		PackedNode node71 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node71.addChild(node55);
		node71.addChild(node38);
		node1.addChild(node2);
		node1.addChild(node45);
		node1.addChild(node63);
		node1.addChild(node64);
		node1.addChild(node69);
		node1.addChild(node70);
		node1.addChild(node71);
		return node1;
	}

}
