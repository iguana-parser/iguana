package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.jgll.util.generator.CompilationUtil;
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
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(31, parseStatistics.getDescriptorsCount());
		assertEquals(6, parseStatistics.getNonterminalNodesCount());
		assertEquals(3, parseStatistics.getIntermediateNodesCount());
		assertEquals(12, parseStatistics.getPackedNodesCount());
		assertEquals(3, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated1() {
		Input input = Input.fromString("bbb");
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());		
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(31, parseStatistics.getDescriptorsCount());
		assertEquals(6, parseStatistics.getNonterminalNodesCount());
		assertEquals(3, parseStatistics.getIntermediateNodesCount());
		assertEquals(12, parseStatistics.getPackedNodesCount());
		assertEquals(3, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1(parser.getRegistry())));
	}
	
	@Test
	public void testParsers2() {
		Input input = Input.fromString("bbbb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(50, parseStatistics.getDescriptorsCount());
		assertEquals(10, parseStatistics.getNonterminalNodesCount());
		assertEquals(6, parseStatistics.getIntermediateNodesCount());
		assertEquals(28, parseStatistics.getPackedNodesCount());
		assertEquals(4, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF2(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated2() {
		Input input = Input.fromString("bbbb");
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());		
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(31, parseStatistics.getDescriptorsCount());
		assertEquals(10, parseStatistics.getNonterminalNodesCount());
		assertEquals(6, parseStatistics.getIntermediateNodesCount());
		assertEquals(28, parseStatistics.getPackedNodesCount());
		assertEquals(4, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF2(parser.getRegistry())));
	}
	
	@Test
	public void testParsers3() {
		Input input = Input.fromString("bbbbb");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(74, parseStatistics.getDescriptorsCount());
		assertEquals(15, parseStatistics.getNonterminalNodesCount());
		assertEquals(10, parseStatistics.getIntermediateNodesCount());
		assertEquals(55, parseStatistics.getPackedNodesCount());
		assertEquals(5, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF3(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated3() {
		Input input = Input.fromString("bbbbb");
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());		
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(74, parseStatistics.getDescriptorsCount());
		assertEquals(15, parseStatistics.getNonterminalNodesCount());
		assertEquals(10, parseStatistics.getIntermediateNodesCount());
		assertEquals(55, parseStatistics.getPackedNodesCount());
		assertEquals(5, parseStatistics.getTerminalNodesCount());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF3(parser.getRegistry())));
	}
	
	@Test
	public void testParsers4() {
		Input input = Input.fromString(getBs(100));
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(25154, parseStatistics.getDescriptorsCount());
		assertEquals(5050, parseStatistics.getNonterminalNodesCount());
		assertEquals(4950, parseStatistics.getIntermediateNodesCount());
		assertEquals(100, parseStatistics.getTerminalNodesCount());
		assertEquals(495100, parseStatistics.getPackedNodesCount());
	}
	
	@Test
	public void testGenerated4() {
		Input input = Input.fromString(getBs(100));
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());		
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
//		assertEquals(25154, parseStatistics.getDescriptorsCount());
		assertEquals(5050, parseStatistics.getNonterminalNodesCount());
		assertEquals(4950, parseStatistics.getIntermediateNodesCount());
		assertEquals(100, parseStatistics.getTerminalNodesCount());
		assertEquals(495100, parseStatistics.getPackedNodesCount());
	}
	
	private String getBs(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF1(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node4 = factory.createPackedNode("S ::= S S .", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("S ::= b .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("S ::= b .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node12 = factory.createPackedNode("S ::= b .", 2, node11);
		TerminalNode node13 = factory.createTerminalNode("b", 2, 3);
		node12.addChild(node13);
		node11.addChild(node12);
		node2.addChild(node3);
		node2.addChild(node11);
		PackedNode node14 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node16 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node17 = factory.createPackedNode("S ::= S S .", 2, node16);
		node17.addChild(node8);
		node17.addChild(node11);
		node16.addChild(node17);
		node14.addChild(node5);
		node14.addChild(node16);
		PackedNode node20 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node21 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node22 = factory.createPackedNode("S ::= S S . S", 1, node21);
		node22.addChild(node5);
		node22.addChild(node8);
		node21.addChild(node22);
		node20.addChild(node21);
		node20.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node14);
		node1.addChild(node20);
		return node1;
	}
	
	private SPPFNode getSPPF2(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 4).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 3, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node4 = factory.createPackedNode("S ::= S S .", 2, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node6 = factory.createPackedNode("S ::= S S .", 1, node5);
		NonterminalNode node7 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node8 = factory.createPackedNode("S ::= b .", 0, node7);
		TerminalNode node9 = factory.createTerminalNode("b", 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalNode node10 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node11 = factory.createPackedNode("S ::= b .", 1, node10);
		TerminalNode node12 = factory.createTerminalNode("b", 1, 2);
		node11.addChild(node12);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		node5.addChild(node6);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("S ::= b .", 2, node13);
		TerminalNode node15 = factory.createTerminalNode("b", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		node4.addChild(node5);
		node4.addChild(node13);
		PackedNode node16 = factory.createPackedNode("S ::= S S .", 1, node3);
		NonterminalNode node18 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node19 = factory.createPackedNode("S ::= S S .", 2, node18);
		node19.addChild(node10);
		node19.addChild(node13);
		node18.addChild(node19);
		node16.addChild(node7);
		node16.addChild(node18);
		PackedNode node22 = factory.createPackedNode("S ::= S S S .", 2, node3);
		IntermediateNode node23 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node24 = factory.createPackedNode("S ::= S S . S", 1, node23);
		node24.addChild(node7);
		node24.addChild(node10);
		node23.addChild(node24);
		node22.addChild(node23);
		node22.addChild(node13);
		node3.addChild(node4);
		node3.addChild(node16);
		node3.addChild(node22);
		NonterminalNode node28 = factory.createNonterminalNode("S", 0, 3, 4).init();
		PackedNode node29 = factory.createPackedNode("S ::= b .", 3, node28);
		TerminalNode node30 = factory.createTerminalNode("b", 3, 4);
		node29.addChild(node30);
		node28.addChild(node29);
		node2.addChild(node3);
		node2.addChild(node28);
		PackedNode node31 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node33 = factory.createNonterminalNode("S", 0, 2, 4).init();
		PackedNode node34 = factory.createPackedNode("S ::= S S .", 3, node33);
		node34.addChild(node13);
		node34.addChild(node28);
		node33.addChild(node34);
		node31.addChild(node5);
		node31.addChild(node33);
		PackedNode node37 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node39 = factory.createNonterminalNode("S", 0, 1, 4).init();
		PackedNode node40 = factory.createPackedNode("S ::= S S .", 2, node39);
		node40.addChild(node10);
		node40.addChild(node33);
		PackedNode node43 = factory.createPackedNode("S ::= S S .", 3, node39);
		node43.addChild(node18);
		node43.addChild(node28);
		PackedNode node46 = factory.createPackedNode("S ::= S S S .", 3, node39);
		IntermediateNode node47 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node48 = factory.createPackedNode("S ::= S S . S", 2, node47);
		node48.addChild(node10);
		node48.addChild(node13);
		node47.addChild(node48);
		node46.addChild(node47);
		node46.addChild(node28);
		node39.addChild(node40);
		node39.addChild(node43);
		node39.addChild(node46);
		node37.addChild(node7);
		node37.addChild(node39);
		PackedNode node52 = factory.createPackedNode("S ::= S S S .", 3, node1);
		IntermediateNode node53 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node54 = factory.createPackedNode("S ::= S S . S", 2, node53);
		node54.addChild(node5);
		node54.addChild(node13);
		PackedNode node57 = factory.createPackedNode("S ::= S S . S", 1, node53);
		node57.addChild(node7);
		node57.addChild(node18);
		node53.addChild(node54);
		node53.addChild(node57);
		node52.addChild(node53);
		node52.addChild(node28);
		PackedNode node61 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node61.addChild(node23);
		node61.addChild(node33);
		node1.addChild(node2);
		node1.addChild(node31);
		node1.addChild(node37);
		node1.addChild(node52);
		node1.addChild(node61);
		return node1;
	}
	
	private SPPFNode getSPPF3(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 4, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 0, 4).init();
		PackedNode node4 = factory.createPackedNode("S ::= S S .", 3, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node6 = factory.createPackedNode("S ::= S S .", 2, node5);
		NonterminalNode node7 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node8 = factory.createPackedNode("S ::= S S .", 1, node7);
		NonterminalNode node9 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node10 = factory.createPackedNode("S ::= b .", 0, node9);
		TerminalNode node11 = factory.createTerminalNode("b", 0, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalNode node12 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node13 = factory.createPackedNode("S ::= b .", 1, node12);
		TerminalNode node14 = factory.createTerminalNode("b", 1, 2);
		node13.addChild(node14);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node7.addChild(node8);
		NonterminalNode node15 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node16 = factory.createPackedNode("S ::= b .", 2, node15);
		TerminalNode node17 = factory.createTerminalNode("b", 2, 3);
		node16.addChild(node17);
		node15.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node15);
		PackedNode node18 = factory.createPackedNode("S ::= S S .", 1, node5);
		NonterminalNode node20 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node21 = factory.createPackedNode("S ::= S S .", 2, node20);
		node21.addChild(node12);
		node21.addChild(node15);
		node20.addChild(node21);
		node18.addChild(node9);
		node18.addChild(node20);
		PackedNode node24 = factory.createPackedNode("S ::= S S S .", 2, node5);
		IntermediateNode node25 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node26 = factory.createPackedNode("S ::= S S . S", 1, node25);
		node26.addChild(node9);
		node26.addChild(node12);
		node25.addChild(node26);
		node24.addChild(node25);
		node24.addChild(node15);
		node5.addChild(node6);
		node5.addChild(node18);
		node5.addChild(node24);
		NonterminalNode node30 = factory.createNonterminalNode("S", 0, 3, 4).init();
		PackedNode node31 = factory.createPackedNode("S ::= b .", 3, node30);
		TerminalNode node32 = factory.createTerminalNode("b", 3, 4);
		node31.addChild(node32);
		node30.addChild(node31);
		node4.addChild(node5);
		node4.addChild(node30);
		PackedNode node33 = factory.createPackedNode("S ::= S S .", 2, node3);
		NonterminalNode node35 = factory.createNonterminalNode("S", 0, 2, 4).init();
		PackedNode node36 = factory.createPackedNode("S ::= S S .", 3, node35);
		node36.addChild(node15);
		node36.addChild(node30);
		node35.addChild(node36);
		node33.addChild(node7);
		node33.addChild(node35);
		PackedNode node39 = factory.createPackedNode("S ::= S S .", 1, node3);
		NonterminalNode node41 = factory.createNonterminalNode("S", 0, 1, 4).init();
		PackedNode node42 = factory.createPackedNode("S ::= S S .", 2, node41);
		node42.addChild(node12);
		node42.addChild(node35);
		PackedNode node45 = factory.createPackedNode("S ::= S S .", 3, node41);
		node45.addChild(node20);
		node45.addChild(node30);
		PackedNode node48 = factory.createPackedNode("S ::= S S S .", 3, node41);
		IntermediateNode node49 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node50 = factory.createPackedNode("S ::= S S . S", 2, node49);
		node50.addChild(node12);
		node50.addChild(node15);
		node49.addChild(node50);
		node48.addChild(node49);
		node48.addChild(node30);
		node41.addChild(node42);
		node41.addChild(node45);
		node41.addChild(node48);
		node39.addChild(node9);
		node39.addChild(node41);
		PackedNode node54 = factory.createPackedNode("S ::= S S S .", 3, node3);
		IntermediateNode node55 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node56 = factory.createPackedNode("S ::= S S . S", 2, node55);
		node56.addChild(node7);
		node56.addChild(node15);
		PackedNode node59 = factory.createPackedNode("S ::= S S . S", 1, node55);
		node59.addChild(node9);
		node59.addChild(node20);
		node55.addChild(node56);
		node55.addChild(node59);
		node54.addChild(node55);
		node54.addChild(node30);
		PackedNode node63 = factory.createPackedNode("S ::= S S S .", 2, node3);
		node63.addChild(node25);
		node63.addChild(node35);
		node3.addChild(node4);
		node3.addChild(node33);
		node3.addChild(node39);
		node3.addChild(node54);
		node3.addChild(node63);
		NonterminalNode node66 = factory.createNonterminalNode("S", 0, 4, 5).init();
		PackedNode node67 = factory.createPackedNode("S ::= b .", 4, node66);
		TerminalNode node68 = factory.createTerminalNode("b", 4, 5);
		node67.addChild(node68);
		node66.addChild(node67);
		node2.addChild(node3);
		node2.addChild(node66);
		PackedNode node69 = factory.createPackedNode("S ::= S S .", 3, node1);
		NonterminalNode node71 = factory.createNonterminalNode("S", 0, 3, 5).init();
		PackedNode node72 = factory.createPackedNode("S ::= S S .", 4, node71);
		node72.addChild(node30);
		node72.addChild(node66);
		node71.addChild(node72);
		node69.addChild(node5);
		node69.addChild(node71);
		PackedNode node75 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node77 = factory.createNonterminalNode("S", 0, 2, 5).init();
		PackedNode node78 = factory.createPackedNode("S ::= S S .", 3, node77);
		node78.addChild(node15);
		node78.addChild(node71);
		PackedNode node81 = factory.createPackedNode("S ::= S S .", 4, node77);
		node81.addChild(node35);
		node81.addChild(node66);
		PackedNode node84 = factory.createPackedNode("S ::= S S S .", 4, node77);
		IntermediateNode node85 = factory.createIntermediateNode("S ::= S S . S", 2, 4).init();
		PackedNode node86 = factory.createPackedNode("S ::= S S . S", 3, node85);
		node86.addChild(node15);
		node86.addChild(node30);
		node85.addChild(node86);
		node84.addChild(node85);
		node84.addChild(node66);
		node77.addChild(node78);
		node77.addChild(node81);
		node77.addChild(node84);
		node75.addChild(node7);
		node75.addChild(node77);
		PackedNode node90 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node92 = factory.createNonterminalNode("S", 0, 1, 5).init();
		PackedNode node93 = factory.createPackedNode("S ::= S S .", 2, node92);
		node93.addChild(node12);
		node93.addChild(node77);
		PackedNode node96 = factory.createPackedNode("S ::= S S .", 4, node92);
		node96.addChild(node41);
		node96.addChild(node66);
		PackedNode node99 = factory.createPackedNode("S ::= S S .", 3, node92);
		node99.addChild(node20);
		node99.addChild(node71);
		PackedNode node102 = factory.createPackedNode("S ::= S S S .", 4, node92);
		IntermediateNode node103 = factory.createIntermediateNode("S ::= S S . S", 1, 4).init();
		PackedNode node104 = factory.createPackedNode("S ::= S S . S", 3, node103);
		node104.addChild(node20);
		node104.addChild(node30);
		PackedNode node107 = factory.createPackedNode("S ::= S S . S", 2, node103);
		node107.addChild(node12);
		node107.addChild(node35);
		node103.addChild(node104);
		node103.addChild(node107);
		node102.addChild(node103);
		node102.addChild(node66);
		PackedNode node111 = factory.createPackedNode("S ::= S S S .", 3, node92);
		node111.addChild(node49);
		node111.addChild(node71);
		node92.addChild(node93);
		node92.addChild(node96);
		node92.addChild(node99);
		node92.addChild(node102);
		node92.addChild(node111);
		node90.addChild(node9);
		node90.addChild(node92);
		PackedNode node114 = factory.createPackedNode("S ::= S S S .", 4, node1);
		IntermediateNode node115 = factory.createIntermediateNode("S ::= S S . S", 0, 4).init();
		PackedNode node116 = factory.createPackedNode("S ::= S S . S", 3, node115);
		node116.addChild(node5);
		node116.addChild(node30);
		PackedNode node119 = factory.createPackedNode("S ::= S S . S", 2, node115);
		node119.addChild(node7);
		node119.addChild(node35);
		PackedNode node122 = factory.createPackedNode("S ::= S S . S", 1, node115);
		node122.addChild(node9);
		node122.addChild(node41);
		node115.addChild(node116);
		node115.addChild(node119);
		node115.addChild(node122);
		node114.addChild(node115);
		node114.addChild(node66);
		PackedNode node126 = factory.createPackedNode("S ::= S S S .", 3, node1);
		node126.addChild(node55);
		node126.addChild(node71);
		PackedNode node129 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node129.addChild(node25);
		node129.addChild(node77);
		node1.addChild(node2);
		node1.addChild(node69);
		node1.addChild(node75);
		node1.addChild(node90);
		node1.addChild(node114);
		node1.addChild(node126);
		node1.addChild(node129);
		return node1;
	}

}
