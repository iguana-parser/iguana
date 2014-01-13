package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

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
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("gamma2");
		
		Rule rule1 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(new Nonterminal("S"), list(new Nonterminal("S"), new Nonterminal("S")));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(new Nonterminal("S"), list(new Character('b')));
		builder.addRule(rule3);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testParsers1() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("bbb"), grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void testParsers2() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("bbbbb"), grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF2()));
	}
	
	@Test
	public void test100bs() throws ParseError {
		Input input = Input.fromString(get100b());
		parser.parse(input, grammar, "S");
	}
	
	private String get100b() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 100; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 2, node1);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 0, 2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(2, 0, 1);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 1, 1);
		node6.addChild(node7);
		node3.addChild(node4);
		node3.addChild(node6);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(2, 2, 1);
		node8.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node8);
		PackedNode node10 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 2);
		node11.addChild(node4);
		node11.addChild(node6);
		node10.addChild(node11);
		node10.addChild(node8);
		PackedNode node12 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 1, node1);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		node13.addChild(node6);
		node13.addChild(node8);
		node12.addChild(node4);
		node12.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node10);
		node1.addChild(node12);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 4, node1);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 0, 4);
		PackedNode node4 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 3, node3);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node6 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 2, node5);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 0, 2);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 1);
		TokenSymbolNode node9 = new TokenSymbolNode(2, 0, 1);
		node8.addChild(node9);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(2, 1, 1);
		node10.addChild(node11);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(2, 2, 1);
		node12.addChild(node13);
		node6.addChild(node7);
		node6.addChild(node12);
		PackedNode node14 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node5);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 2);
		node15.addChild(node8);
		node15.addChild(node10);
		node14.addChild(node15);
		node14.addChild(node12);
		PackedNode node16 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 1, node5);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		node17.addChild(node10);
		node17.addChild(node12);
		node16.addChild(node8);
		node16.addChild(node17);
		node5.addChild(node6);
		node5.addChild(node14);
		node5.addChild(node16);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 4);
		TokenSymbolNode node19 = new TokenSymbolNode(2, 3, 1);
		node18.addChild(node19);
		node4.addChild(node5);
		node4.addChild(node18);
		PackedNode node20 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 1, node3);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 4);
		PackedNode node22 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 3, node21);
		IntermediateNode node23 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 1, 3);
		node23.addChild(node10);
		node23.addChild(node12);
		node22.addChild(node23);
		node22.addChild(node18);
		PackedNode node24 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 3, node21);
		node24.addChild(node17);
		node24.addChild(node18);
		PackedNode node25 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node21);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 4);
		node26.addChild(node12);
		node26.addChild(node18);
		node25.addChild(node10);
		node25.addChild(node26);
		node21.addChild(node22);
		node21.addChild(node24);
		node21.addChild(node25);
		node20.addChild(node8);
		node20.addChild(node21);
		PackedNode node27 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 2, node3);
		node27.addChild(node15);
		node27.addChild(node26);
		node3.addChild(node4);
		node3.addChild(node20);
		node3.addChild(node27);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5);
		TokenSymbolNode node29 = new TokenSymbolNode(2, 4, 1);
		node28.addChild(node29);
		node2.addChild(node3);
		node2.addChild(node28);
		PackedNode node30 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 4, node1);
		NonterminalSymbolNode node31 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 4);
		PackedNode node32 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 3, node31);
		node32.addChild(node5);
		node32.addChild(node18);
		PackedNode node33 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 3, node31);
		IntermediateNode node34 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 0, 3);
		PackedNode node35 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 2, node34);
		node35.addChild(node15);
		node35.addChild(node12);
		PackedNode node36 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 1, node34);
		node36.addChild(node8);
		node36.addChild(node17);
		node34.addChild(node35);
		node34.addChild(node36);
		node33.addChild(node34);
		node33.addChild(node18);
		PackedNode node37 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 1, node31);
		node37.addChild(node8);
		node37.addChild(node21);
		PackedNode node38 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 2, node31);
		node38.addChild(node7);
		node38.addChild(node26);
		PackedNode node39 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node31);
		node39.addChild(node15);
		node39.addChild(node26);
		node31.addChild(node32);
		node31.addChild(node33);
		node31.addChild(node37);
		node31.addChild(node38);
		node31.addChild(node39);
		node30.addChild(node31);
		node30.addChild(node28);
		PackedNode node40 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 1, node1);
		NonterminalSymbolNode node41 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 5);
		PackedNode node42 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 4, node41);
		IntermediateNode node43 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 1, 4);
		PackedNode node44 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 3, node43);
		node44.addChild(node17);
		node44.addChild(node18);
		PackedNode node45 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S . S"), 2, node43);
		node45.addChild(node10);
		node45.addChild(node26);
		node43.addChild(node44);
		node43.addChild(node45);
		node42.addChild(node43);
		node42.addChild(node28);
		PackedNode node46 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 4, node41);
		node46.addChild(node21);
		node46.addChild(node28);
		PackedNode node47 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node41);
		NonterminalSymbolNode node48 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 5);
		PackedNode node49 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 4, node48);
		IntermediateNode node50 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= S S . S"), 2, 4);
		node50.addChild(node12);
		node50.addChild(node18);
		node49.addChild(node50);
		node49.addChild(node28);
		PackedNode node51 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 4, node48);
		node51.addChild(node26);
		node51.addChild(node28);
		PackedNode node52 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 3, node48);
		NonterminalSymbolNode node53 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 5);
		node53.addChild(node18);
		node53.addChild(node28);
		node52.addChild(node12);
		node52.addChild(node53);
		node48.addChild(node49);
		node48.addChild(node51);
		node48.addChild(node52);
		node47.addChild(node10);
		node47.addChild(node48);
		PackedNode node54 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 3, node41);
		node54.addChild(node23);
		node54.addChild(node53);
		PackedNode node55 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 3, node41);
		node55.addChild(node17);
		node55.addChild(node53);
		node41.addChild(node42);
		node41.addChild(node46);
		node41.addChild(node47);
		node41.addChild(node54);
		node41.addChild(node55);
		node40.addChild(node8);
		node40.addChild(node41);
		PackedNode node56 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 2, node1);
		node56.addChild(node7);
		node56.addChild(node48);
		PackedNode node57 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 2, node1);
		node57.addChild(node15);
		node57.addChild(node48);
		PackedNode node58 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S ."), 3, node1);
		node58.addChild(node5);
		node58.addChild(node53);
		PackedNode node59 = new PackedNode(grammar.getGrammarSlotByName("S ::= S S S ."), 3, node1);
		node59.addChild(node34);
		node59.addChild(node53);
		node1.addChild(node2);
		node1.addChild(node30);
		node1.addChild(node40);
		node1.addChild(node56);
		node1.addChild(node57);
		node1.addChild(node58);
		node1.addChild(node59);
		return node1;
	}

}
