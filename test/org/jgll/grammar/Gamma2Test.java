package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
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
	private Nonterminal S = new Nonterminal("S");
	private Character b = new Character('b');
	
	@Before
	public void init() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("gamma2", factory);
		
		Rule rule1 = new Rule(S, list(S, S, S));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(S, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(b));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParsers1() throws ParseError {
		Input input = Input.fromString("bbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void testParsers2() throws ParseError {
		Input input = Input.fromString("bbbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF2()));
	}
	
	@Test
	public void testParsers3() throws ParseError {
		Input input = Input.fromString("bbbbb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF3()));
	}	
	
	@Test
	public void test100bs() throws ParseError {
		Input input = Input.fromString(get100b());		
		GLLParser parser = ParserFactory.newParser(grammar, input);
		parser.parse(input, grammar, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 200; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 3);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 1, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 2, 1);
		node8.addChild(node9);
		node5.addChild(node6);
		node5.addChild(node8);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node10 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 2);
		node11.addChild(node3);
		node11.addChild(node6);
		node10.addChild(node11);
		node10.addChild(node8);
		PackedNode node12 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node1);
		IntermediateNode node13 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 2);
		node13.addChild(node3);
		node13.addChild(node6);
		node12.addChild(node13);
		node12.addChild(node8);
		node1.addChild(node2);
		node1.addChild(node10);
		node1.addChild(node12);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 4);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 4);
		PackedNode node6 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node5);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 2);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 1, 1);
		node8.addChild(node9);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 2, 1);
		node10.addChild(node11);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 3, 4);
		TokenSymbolNode node13 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 3, 1);
		node12.addChild(node13);
		node6.addChild(node7);
		node6.addChild(node12);
		PackedNode node14 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node5);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 4);
		node15.addChild(node10);
		node15.addChild(node12);
		node14.addChild(node8);
		node14.addChild(node15);
		PackedNode node16 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node5);
		IntermediateNode node17 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 1, 3);
		node17.addChild(node8);
		node17.addChild(node10);
		node16.addChild(node17);
		node16.addChild(node12);
		node5.addChild(node6);
		node5.addChild(node14);
		node5.addChild(node16);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node18 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node1);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 3);
		PackedNode node20 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node19);
		node20.addChild(node3);
		node20.addChild(node7);
		PackedNode node21 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node19);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 2);
		node22.addChild(node3);
		node22.addChild(node8);
		node21.addChild(node22);
		node21.addChild(node10);
		PackedNode node23 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node19);
		IntermediateNode node24 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 2);
		node24.addChild(node3);
		node24.addChild(node8);
		node23.addChild(node24);
		node23.addChild(node10);
		node19.addChild(node20);
		node19.addChild(node21);
		node19.addChild(node23);
		node18.addChild(node19);
		node18.addChild(node12);
		PackedNode node25 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node1);
		node25.addChild(node22);
		node25.addChild(node15);
		PackedNode node26 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node1);
		IntermediateNode node27 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 3);
		PackedNode node28 = new PackedNode(grammar.getIntermediateNodeId(S,S), 2, node27);
		node28.addChild(node22);
		node28.addChild(node10);
		PackedNode node29 = new PackedNode(grammar.getIntermediateNodeId(S,S), 1, node27);
		node29.addChild(node3);
		node29.addChild(node7);
		node27.addChild(node28);
		node27.addChild(node29);
		node26.addChild(node27);
		node26.addChild(node12);
		PackedNode node30 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node1);
		node30.addChild(node24);
		node30.addChild(node15);
		node1.addChild(node2);
		node1.addChild(node18);
		node1.addChild(node25);
		node1.addChild(node26);
		node1.addChild(node30);
		return node1;
	}
	
	private SPPFNode getSPPF3() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 5);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 5);
		PackedNode node6 = new PackedNode(grammar.getPackedNodeId(S, S, S), 4, node5);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 4);
		PackedNode node8 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node7);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 1, 1);
		node10.addChild(node11);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 2, 1);
		node12.addChild(node13);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 3, 4);
		TokenSymbolNode node15 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 3, 1);
		node14.addChild(node15);
		node8.addChild(node9);
		node8.addChild(node14);
		PackedNode node16 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node7);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 4);
		node17.addChild(node12);
		node17.addChild(node14);
		node16.addChild(node10);
		node16.addChild(node17);
		PackedNode node18 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node7);
		IntermediateNode node19 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 1, 3);
		node19.addChild(node10);
		node19.addChild(node12);
		node18.addChild(node19);
		node18.addChild(node14);
		node7.addChild(node8);
		node7.addChild(node16);
		node7.addChild(node18);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 4, 5);
		TokenSymbolNode node21 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 4, 1);
		node20.addChild(node21);
		node6.addChild(node7);
		node6.addChild(node20);
		PackedNode node22 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node5);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 3, 5);
		node23.addChild(node14);
		node23.addChild(node20);
		node22.addChild(node9);
		node22.addChild(node23);
		PackedNode node24 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node5);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 5);
		PackedNode node26 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node25);
		node26.addChild(node12);
		node26.addChild(node23);
		PackedNode node27 = new PackedNode(grammar.getPackedNodeId(S, S, S), 4, node25);
		node27.addChild(node17);
		node27.addChild(node20);
		PackedNode node28 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 4, node25);
		IntermediateNode node29 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 2, 4);
		node29.addChild(node12);
		node29.addChild(node14);
		node28.addChild(node29);
		node28.addChild(node20);
		node25.addChild(node26);
		node25.addChild(node27);
		node25.addChild(node28);
		node24.addChild(node10);
		node24.addChild(node25);
		PackedNode node30 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 4, node5);
		IntermediateNode node31 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 1, 4);
		PackedNode node32 = new PackedNode(grammar.getIntermediateNodeId(S,S), 3, node31);
		node32.addChild(node9);
		node32.addChild(node14);
		PackedNode node33 = new PackedNode(grammar.getIntermediateNodeId(S,S), 2, node31);
		node33.addChild(node10);
		node33.addChild(node17);
		node31.addChild(node32);
		node31.addChild(node33);
		node30.addChild(node31);
		node30.addChild(node20);
		PackedNode node34 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node5);
		node34.addChild(node19);
		node34.addChild(node23);
		node5.addChild(node6);
		node5.addChild(node22);
		node5.addChild(node24);
		node5.addChild(node30);
		node5.addChild(node34);
		node2.addChild(node3);
		node2.addChild(node5);
		PackedNode node35 = new PackedNode(grammar.getPackedNodeId(S, S, S), 4, node1);
		NonterminalSymbolNode node36 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 4);
		PackedNode node37 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node36);
		node37.addChild(node3);
		node37.addChild(node7);
		PackedNode node38 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node36);
		NonterminalSymbolNode node39 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 3);
		PackedNode node40 = new PackedNode(grammar.getPackedNodeId(S, S, S), 1, node39);
		node40.addChild(node3);
		node40.addChild(node9);
		PackedNode node41 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node39);
		NonterminalSymbolNode node42 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 2);
		node42.addChild(node3);
		node42.addChild(node10);
		node41.addChild(node42);
		node41.addChild(node12);
		PackedNode node43 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node39);
		IntermediateNode node44 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 2);
		node44.addChild(node3);
		node44.addChild(node10);
		node43.addChild(node44);
		node43.addChild(node12);
		node39.addChild(node40);
		node39.addChild(node41);
		node39.addChild(node43);
		node38.addChild(node39);
		node38.addChild(node14);
		PackedNode node45 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node36);
		node45.addChild(node42);
		node45.addChild(node17);
		PackedNode node46 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node36);
		IntermediateNode node47 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 3);
		PackedNode node48 = new PackedNode(grammar.getIntermediateNodeId(S,S), 2, node47);
		node48.addChild(node42);
		node48.addChild(node12);
		PackedNode node49 = new PackedNode(grammar.getIntermediateNodeId(S,S), 1, node47);
		node49.addChild(node3);
		node49.addChild(node9);
		node47.addChild(node48);
		node47.addChild(node49);
		node46.addChild(node47);
		node46.addChild(node14);
		PackedNode node50 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node36);
		node50.addChild(node44);
		node50.addChild(node17);
		node36.addChild(node37);
		node36.addChild(node38);
		node36.addChild(node45);
		node36.addChild(node46);
		node36.addChild(node50);
		node35.addChild(node36);
		node35.addChild(node20);
		PackedNode node51 = new PackedNode(grammar.getPackedNodeId(S, S, S), 3, node1);
		node51.addChild(node39);
		node51.addChild(node23);
		PackedNode node52 = new PackedNode(grammar.getPackedNodeId(S, S, S), 2, node1);
		node52.addChild(node42);
		node52.addChild(node25);
		PackedNode node53 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 4, node1);
		IntermediateNode node54 = new IntermediateNode(grammar.getIntermediateNodeId(S,S), 0, 4);
		PackedNode node55 = new PackedNode(grammar.getIntermediateNodeId(S,S), 2, node54);
		node55.addChild(node42);
		node55.addChild(node17);
		PackedNode node56 = new PackedNode(grammar.getIntermediateNodeId(S,S), 3, node54);
		node56.addChild(node39);
		node56.addChild(node14);
		PackedNode node57 = new PackedNode(grammar.getIntermediateNodeId(S,S), 1, node54);
		node57.addChild(node3);
		node57.addChild(node7);
		node54.addChild(node55);
		node54.addChild(node56);
		node54.addChild(node57);
		node53.addChild(node54);
		node53.addChild(node20);
		PackedNode node58 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 3, node1);
		node58.addChild(node47);
		node58.addChild(node23);
		PackedNode node59 = new PackedNode(grammar.getPackedNodeId(S, S, S, S), 2, node1);
		node59.addChild(node44);
		node59.addChild(node25);
		node1.addChild(node2);
		node1.addChild(node35);
		node1.addChild(node51);
		node1.addChild(node52);
		node1.addChild(node53);
		node1.addChild(node58);
		node1.addChild(node59);
		return node1;
	}

}
