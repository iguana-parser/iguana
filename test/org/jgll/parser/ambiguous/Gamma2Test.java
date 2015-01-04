package org.jgll.parser.ambiguous;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
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
import org.jgll.sppf.TerminalNode;
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
		Rule rule1 = Rule.builder(S).addSymbols(S, S, S).build();
		Rule rule2 = Rule.builder(S).addSymbols(S, S).build();
		Rule rule3 = Rule.builder(S).addSymbols(b).build();
		
		grammar = Grammar.builder().addRules(rule1, rule2, rule3).build();
	}
	
	@Test
	public void testParsers1() {
		Input input = Input.fromString("bbb");
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "S");
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
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "S");
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
	public void testParsers3() {
		Input input = Input.fromString("bbbbb");
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "S");
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
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "S");
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
	
	private SPPFNode getSPPF1(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("S ::= S S . S", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("S ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("S ::= b .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node12 = factory.createPackedNode("S ::= b .", 3, node11);
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
		PackedNode node20 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node21 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node22 = factory.createPackedNode("S ::= S S .", 1, node21);
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
	
	private SPPFNode getSPPF2(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 4).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S S .", 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node4 = factory.createPackedNode("S ::= S S . S", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("S ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node9 = factory.createPackedNode("S ::= S S .", 2, node8);
		NonterminalNode node10 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node11 = factory.createPackedNode("S ::= b .", 2, node10);
		TerminalNode node12 = factory.createTerminalNode("b", 1, 2);
		node11.addChild(node12);
		node10.addChild(node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("S ::= b .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("b", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		node9.addChild(node10);
		node9.addChild(node13);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		PackedNode node16 = factory.createPackedNode("S ::= S S . S", 2, node3);
		NonterminalNode node17 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node18 = factory.createPackedNode("S ::= S S .", 1, node17);
		node18.addChild(node5);
		node18.addChild(node10);
		node17.addChild(node18);
		node16.addChild(node17);
		node16.addChild(node13);
		node3.addChild(node4);
		node3.addChild(node16);
		NonterminalNode node22 = factory.createNonterminalNode("S", 0, 3, 4).init();
		PackedNode node23 = factory.createPackedNode("S ::= b .", 4, node22);
		TerminalNode node24 = factory.createTerminalNode("b", 3, 4);
		node23.addChild(node24);
		node22.addChild(node23);
		node2.addChild(node3);
		node2.addChild(node22);
		PackedNode node25 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node26 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node27 = factory.createPackedNode("S ::= S S . S", 1, node26);
		node27.addChild(node5);
		node27.addChild(node10);
		node26.addChild(node27);
		NonterminalNode node30 = factory.createNonterminalNode("S", 0, 2, 4).init();
		PackedNode node31 = factory.createPackedNode("S ::= S S .", 3, node30);
		node31.addChild(node13);
		node31.addChild(node22);
		node30.addChild(node31);
		node25.addChild(node26);
		node25.addChild(node30);
		PackedNode node34 = factory.createPackedNode("S ::= S S .", 3, node1);
		NonterminalNode node35 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node36 = factory.createPackedNode("S ::= S S S .", 2, node35);
		node36.addChild(node26);
		node36.addChild(node13);
		PackedNode node39 = factory.createPackedNode("S ::= S S .", 1, node35);
		node39.addChild(node5);
		node39.addChild(node8);
		PackedNode node42 = factory.createPackedNode("S ::= S S .", 2, node35);
		node42.addChild(node17);
		node42.addChild(node13);
		node35.addChild(node36);
		node35.addChild(node39);
		node35.addChild(node42);
		node34.addChild(node35);
		node34.addChild(node22);
		PackedNode node46 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node48 = factory.createNonterminalNode("S", 0, 1, 4).init();
		PackedNode node49 = factory.createPackedNode("S ::= S S S .", 3, node48);
		IntermediateNode node50 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node51 = factory.createPackedNode("S ::= S S . S", 2, node50);
		node51.addChild(node10);
		node51.addChild(node13);
		node50.addChild(node51);
		node49.addChild(node50);
		node49.addChild(node22);
		PackedNode node55 = factory.createPackedNode("S ::= S S .", 2, node48);
		node55.addChild(node10);
		node55.addChild(node30);
		PackedNode node58 = factory.createPackedNode("S ::= S S .", 3, node48);
		node58.addChild(node8);
		node58.addChild(node22);
		node48.addChild(node49);
		node48.addChild(node55);
		node48.addChild(node58);
		node46.addChild(node5);
		node46.addChild(node48);
		PackedNode node61 = factory.createPackedNode("S ::= S S .", 2, node1);
		node61.addChild(node17);
		node61.addChild(node30);
		node1.addChild(node2);
		node1.addChild(node25);
		node1.addChild(node34);
		node1.addChild(node46);
		node1.addChild(node61);
		return node1;
	}
	
	private SPPFNode getSPPF3(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5).init();
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= b .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 5).init();
		PackedNode node7 = factory.createPackedNode("S ::= S S .", 2, node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("S ::= b .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 5).init();
		PackedNode node12 = factory.createPackedNode("S ::= S S .", 3, node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("S ::= b .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("b", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		NonterminalNode node16 = factory.createNonterminalNode("S", 0, 3, 5).init();
		PackedNode node17 = factory.createPackedNode("S ::= S S .", 4, node16);
		NonterminalNode node18 = factory.createNonterminalNode("S", 0, 3, 4).init();
		PackedNode node19 = factory.createPackedNode("S ::= b .", 4, node18);
		TerminalNode node20 = factory.createTerminalNode("b", 3, 4);
		node19.addChild(node20);
		node18.addChild(node19);
		NonterminalNode node21 = factory.createNonterminalNode("S", 0, 4, 5).init();
		PackedNode node22 = factory.createPackedNode("S ::= b .", 5, node21);
		TerminalNode node23 = factory.createTerminalNode("b", 4, 5);
		node22.addChild(node23);
		node21.addChild(node22);
		node17.addChild(node18);
		node17.addChild(node21);
		node16.addChild(node17);
		node12.addChild(node13);
		node12.addChild(node16);
		PackedNode node24 = factory.createPackedNode("S ::= S S .", 4, node11);
		NonterminalNode node25 = factory.createNonterminalNode("S", 0, 2, 4).init();
		PackedNode node26 = factory.createPackedNode("S ::= S S .", 3, node25);
		node26.addChild(node13);
		node26.addChild(node18);
		node25.addChild(node26);
		node24.addChild(node25);
		node24.addChild(node21);
		PackedNode node30 = factory.createPackedNode("S ::= S S S .", 4, node11);
		IntermediateNode node31 = factory.createIntermediateNode("S ::= S S . S", 2, 4).init();
		PackedNode node32 = factory.createPackedNode("S ::= S S . S", 3, node31);
		node32.addChild(node13);
		node32.addChild(node18);
		node31.addChild(node32);
		node30.addChild(node31);
		node30.addChild(node21);
		node11.addChild(node12);
		node11.addChild(node24);
		node11.addChild(node30);
		node7.addChild(node8);
		node7.addChild(node11);
		PackedNode node36 = factory.createPackedNode("S ::= S S .", 4, node6);
		NonterminalNode node37 = factory.createNonterminalNode("S", 0, 1, 4).init();
		PackedNode node38 = factory.createPackedNode("S ::= S S .", 2, node37);
		node38.addChild(node8);
		node38.addChild(node25);
		PackedNode node41 = factory.createPackedNode("S ::= S S .", 3, node37);
		NonterminalNode node42 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node43 = factory.createPackedNode("S ::= S S .", 2, node42);
		node43.addChild(node8);
		node43.addChild(node13);
		node42.addChild(node43);
		node41.addChild(node42);
		node41.addChild(node18);
		PackedNode node47 = factory.createPackedNode("S ::= S S S .", 3, node37);
		IntermediateNode node48 = factory.createIntermediateNode("S ::= S S . S", 1, 3).init();
		PackedNode node49 = factory.createPackedNode("S ::= S S . S", 2, node48);
		node49.addChild(node8);
		node49.addChild(node13);
		node48.addChild(node49);
		node47.addChild(node48);
		node47.addChild(node18);
		node37.addChild(node38);
		node37.addChild(node41);
		node37.addChild(node47);
		node36.addChild(node37);
		node36.addChild(node21);
		PackedNode node54 = factory.createPackedNode("S ::= S S .", 3, node6);
		node54.addChild(node42);
		node54.addChild(node16);
		PackedNode node57 = factory.createPackedNode("S ::= S S S .", 4, node6);
		IntermediateNode node58 = factory.createIntermediateNode("S ::= S S . S", 1, 4).init();
		PackedNode node59 = factory.createPackedNode("S ::= S S . S", 3, node58);
		node59.addChild(node42);
		node59.addChild(node18);
		PackedNode node62 = factory.createPackedNode("S ::= S S . S", 2, node58);
		node62.addChild(node8);
		node62.addChild(node25);
		node58.addChild(node59);
		node58.addChild(node62);
		node57.addChild(node58);
		node57.addChild(node21);
		PackedNode node66 = factory.createPackedNode("S ::= S S S .", 3, node6);
		node66.addChild(node48);
		node66.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node36);
		node6.addChild(node54);
		node6.addChild(node57);
		node6.addChild(node66);
		node2.addChild(node3);
		node2.addChild(node6);
		PackedNode node69 = factory.createPackedNode("S ::= S S .", 4, node1);
		NonterminalNode node70 = factory.createNonterminalNode("S", 0, 0, 4).init();
		PackedNode node71 = factory.createPackedNode("S ::= S S .", 1, node70);
		node71.addChild(node3);
		node71.addChild(node37);
		PackedNode node74 = factory.createPackedNode("S ::= S S .", 3, node70);
		NonterminalNode node75 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node76 = factory.createPackedNode("S ::= S S .", 1, node75);
		node76.addChild(node3);
		node76.addChild(node42);
		PackedNode node79 = factory.createPackedNode("S ::= S S .", 2, node75);
		NonterminalNode node80 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node81 = factory.createPackedNode("S ::= S S .", 1, node80);
		node81.addChild(node3);
		node81.addChild(node8);
		node80.addChild(node81);
		node79.addChild(node80);
		node79.addChild(node13);
		PackedNode node85 = factory.createPackedNode("S ::= S S S .", 2, node75);
		IntermediateNode node86 = factory.createIntermediateNode("S ::= S S . S", 0, 2).init();
		PackedNode node87 = factory.createPackedNode("S ::= S S . S", 1, node86);
		node87.addChild(node3);
		node87.addChild(node8);
		node86.addChild(node87);
		node85.addChild(node86);
		node85.addChild(node13);
		node75.addChild(node76);
		node75.addChild(node79);
		node75.addChild(node85);
		node74.addChild(node75);
		node74.addChild(node18);
		PackedNode node92 = factory.createPackedNode("S ::= S S .", 2, node70);
		node92.addChild(node80);
		node92.addChild(node25);
		PackedNode node95 = factory.createPackedNode("S ::= S S S .", 3, node70);
		IntermediateNode node96 = factory.createIntermediateNode("S ::= S S . S", 0, 3).init();
		PackedNode node97 = factory.createPackedNode("S ::= S S . S", 2, node96);
		node97.addChild(node80);
		node97.addChild(node13);
		PackedNode node100 = factory.createPackedNode("S ::= S S . S", 1, node96);
		node100.addChild(node3);
		node100.addChild(node42);
		node96.addChild(node97);
		node96.addChild(node100);
		node95.addChild(node96);
		node95.addChild(node18);
		PackedNode node104 = factory.createPackedNode("S ::= S S S .", 2, node70);
		node104.addChild(node86);
		node104.addChild(node25);
		node70.addChild(node71);
		node70.addChild(node74);
		node70.addChild(node92);
		node70.addChild(node95);
		node70.addChild(node104);
		node69.addChild(node70);
		node69.addChild(node21);
		PackedNode node108 = factory.createPackedNode("S ::= S S .", 3, node1);
		node108.addChild(node75);
		node108.addChild(node16);
		PackedNode node111 = factory.createPackedNode("S ::= S S S .", 4, node1);
		IntermediateNode node112 = factory.createIntermediateNode("S ::= S S . S", 0, 4).init();
		PackedNode node113 = factory.createPackedNode("S ::= S S . S", 3, node112);
		node113.addChild(node75);
		node113.addChild(node18);
		PackedNode node116 = factory.createPackedNode("S ::= S S . S", 2, node112);
		node116.addChild(node80);
		node116.addChild(node25);
		PackedNode node119 = factory.createPackedNode("S ::= S S . S", 1, node112);
		node119.addChild(node3);
		node119.addChild(node37);
		node112.addChild(node113);
		node112.addChild(node116);
		node112.addChild(node119);
		node111.addChild(node112);
		node111.addChild(node21);
		PackedNode node123 = factory.createPackedNode("S ::= S S .", 2, node1);
		node123.addChild(node80);
		node123.addChild(node11);
		PackedNode node126 = factory.createPackedNode("S ::= S S S .", 3, node1);
		node126.addChild(node96);
		node126.addChild(node16);
		PackedNode node129 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node129.addChild(node86);
		node129.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node69);
		node1.addChild(node108);
		node1.addChild(node111);
		node1.addChild(node123);
		node1.addChild(node126);
		node1.addChild(node129);
		return node1;
	}

}
