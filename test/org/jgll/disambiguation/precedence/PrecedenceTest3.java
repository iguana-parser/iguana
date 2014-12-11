package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E E+    (non-assoc)
 *     > E + E	 (left)
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest3 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal EPlus = new Nonterminal.Builder("E+").setEbnfList(true).build();
	private Character a = Character.from('a');
	private Character plus = Character.from('+');

	private Grammar grammar;
	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E E+
		Rule rule1 = new Rule(E, list(E, EPlus));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(EPlus, list(EPlus, E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(EPlus, list(E));
		builder.addRule(rule5);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E ::= .E E+, E E+)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E + .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		// (E+ ::= E+ .E, E ::= E+ E)
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule2);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule2);
		
		grammar = operatorPrecedence.transform(builder.build());
		System.out.println(grammar);
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
        assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
        Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), grammar.toGrammarGraph(), input);
//		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	private NonterminalNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 14).init();
		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 10, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 10).init();
		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 9, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 9).init();
		PackedNode node6 = factory.createPackedNode("E ::= E + E2 .", 4, node5);
		IntermediateNode node7 = factory.createIntermediateNode("E ::= E + . E2", 0, 4).init();
		PackedNode node8 = factory.createPackedNode("E ::= E + . E2", 3, node7);
		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 0, 3).init();
		PackedNode node10 = factory.createPackedNode("E ::= E1 E+1 .", 1, node9);
		NonterminalNode node11 = factory.createNonterminalNode("E", 1, 0, 1).init();
		PackedNode node12 = factory.createPackedNode("E1 ::= a .", 0, node11);
		TerminalNode node13 = factory.createTokenNode("a", 0, 1);
		node12.addChild(node13);
		node11.addChild(node12);
		ListSymbolNode node14 = factory.createListNode("E+", 1, 1, 3).init();
		PackedNode node15 = factory.createPackedNode("E+1 ::= E+1 E1 .", 2, node14);
		ListSymbolNode node16 = factory.createListNode("E+", 1, 1, 2).init();
		PackedNode node17 = factory.createPackedNode("E+1 ::= E1 .", 1, node16);
		NonterminalNode node18 = factory.createNonterminalNode("E", 1, 1, 2).init();
		PackedNode node19 = factory.createPackedNode("E1 ::= a .", 1, node18);
		TerminalNode node20 = factory.createTokenNode("a", 1, 1);
		node19.addChild(node20);
		node18.addChild(node19);
		node17.addChild(node18);
		node16.addChild(node17);
		NonterminalNode node21 = factory.createNonterminalNode("E", 1, 2, 3).init();
		PackedNode node22 = factory.createPackedNode("E1 ::= a .", 2, node21);
		TerminalNode node23 = factory.createTokenNode("a", 2, 1);
		node22.addChild(node23);
		node21.addChild(node22);
		node15.addChild(node16);
		node15.addChild(node21);
		node14.addChild(node15);
		node10.addChild(node11);
		node10.addChild(node14);
		node9.addChild(node10);
		TerminalNode node24 = factory.createTokenNode("+", 3, 1);
		node8.addChild(node9);
		node8.addChild(node24);
		node7.addChild(node8);
		NonterminalNode node25 = factory.createNonterminalNode("E", 2, 4, 9).init();
		PackedNode node26 = factory.createPackedNode("E2 ::= E1 E+1 .", 5, node25);
		NonterminalNode node27 = factory.createNonterminalNode("E", 1, 4, 5).init();
		PackedNode node28 = factory.createPackedNode("E1 ::= a .", 4, node27);
		TerminalNode node29 = factory.createTokenNode("a", 4, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		ListSymbolNode node30 = factory.createListNode("E+", 1, 5, 9).init();
		PackedNode node31 = factory.createPackedNode("E+1 ::= E+1 E1 .", 8, node30);
		ListSymbolNode node32 = factory.createListNode("E+", 1, 5, 8).init();
		PackedNode node33 = factory.createPackedNode("E+1 ::= E+1 E1 .", 7, node32);
		ListSymbolNode node34 = factory.createListNode("E+", 1, 5, 7).init();
		PackedNode node35 = factory.createPackedNode("E+1 ::= E+1 E1 .", 6, node34);
		ListSymbolNode node36 = factory.createListNode("E+", 1, 5, 6).init();
		PackedNode node37 = factory.createPackedNode("E+1 ::= E1 .", 5, node36);
		NonterminalNode node38 = factory.createNonterminalNode("E", 1, 5, 6).init();
		PackedNode node39 = factory.createPackedNode("E1 ::= a .", 5, node38);
		TerminalNode node40 = factory.createTokenNode("a", 5, 1);
		node39.addChild(node40);
		node38.addChild(node39);
		node37.addChild(node38);
		node36.addChild(node37);
		NonterminalNode node41 = factory.createNonterminalNode("E", 1, 6, 7).init();
		PackedNode node42 = factory.createPackedNode("E1 ::= a .", 6, node41);
		TerminalNode node43 = factory.createTokenNode("a", 6, 1);
		node42.addChild(node43);
		node41.addChild(node42);
		node35.addChild(node36);
		node35.addChild(node41);
		node34.addChild(node35);
		NonterminalNode node44 = factory.createNonterminalNode("E", 1, 7, 8).init();
		PackedNode node45 = factory.createPackedNode("E1 ::= a .", 7, node44);
		TerminalNode node46 = factory.createTokenNode("a", 7, 1);
		node45.addChild(node46);
		node44.addChild(node45);
		node33.addChild(node34);
		node33.addChild(node44);
		node32.addChild(node33);
		NonterminalNode node47 = factory.createNonterminalNode("E", 1, 8, 9).init();
		PackedNode node48 = factory.createPackedNode("E1 ::= a .", 8, node47);
		TerminalNode node49 = factory.createTokenNode("a", 8, 1);
		node48.addChild(node49);
		node47.addChild(node48);
		node31.addChild(node32);
		node31.addChild(node47);
		node30.addChild(node31);
		node26.addChild(node27);
		node26.addChild(node30);
		node25.addChild(node26);
		node6.addChild(node7);
		node6.addChild(node25);
		node5.addChild(node6);
		TerminalNode node50 = factory.createTokenNode("+", 9, 1);
		node4.addChild(node5);
		node4.addChild(node50);
		node3.addChild(node4);
		NonterminalNode node51 = factory.createNonterminalNode("E", 2, 10, 14).init();
		PackedNode node52 = factory.createPackedNode("E2 ::= E1 E+1 .", 11, node51);
		NonterminalNode node53 = factory.createNonterminalNode("E", 1, 10, 11).init();
		PackedNode node54 = factory.createPackedNode("E1 ::= a .", 10, node53);
		TerminalNode node55 = factory.createTokenNode("a", 10, 1);
		node54.addChild(node55);
		node53.addChild(node54);
		ListSymbolNode node56 = factory.createListNode("E+", 1, 11, 14).init();
		PackedNode node57 = factory.createPackedNode("E+1 ::= E+1 E1 .", 13, node56);
		ListSymbolNode node58 = factory.createListNode("E+", 1, 11, 13).init();
		PackedNode node59 = factory.createPackedNode("E+1 ::= E+1 E1 .", 12, node58);
		ListSymbolNode node60 = factory.createListNode("E+", 1, 11, 12).init();
		PackedNode node61 = factory.createPackedNode("E+1 ::= E1 .", 11, node60);
		NonterminalNode node62 = factory.createNonterminalNode("E", 1, 11, 12).init();
		PackedNode node63 = factory.createPackedNode("E1 ::= a .", 11, node62);
		TerminalNode node64 = factory.createTokenNode("a", 11, 1);
		node63.addChild(node64);
		node62.addChild(node63);
		node61.addChild(node62);
		node60.addChild(node61);
		NonterminalNode node65 = factory.createNonterminalNode("E", 1, 12, 13).init();
		PackedNode node66 = factory.createPackedNode("E1 ::= a .", 12, node65);
		TerminalNode node67 = factory.createTokenNode("a", 12, 1);
		node66.addChild(node67);
		node65.addChild(node66);
		node59.addChild(node60);
		node59.addChild(node65);
		node58.addChild(node59);
		NonterminalNode node68 = factory.createNonterminalNode("E", 1, 13, 14).init();
		PackedNode node69 = factory.createPackedNode("E1 ::= a .", 13, node68);
		TerminalNode node70 = factory.createTokenNode("a", 13, 1);
		node69.addChild(node70);
		node68.addChild(node69);
		node57.addChild(node58);
		node57.addChild(node68);
		node56.addChild(node57);
		node52.addChild(node53);
		node52.addChild(node56);
		node51.addChild(node52);
		node2.addChild(node3);
		node2.addChild(node51);
		node1.addChild(node2);
		return node1;
	}

}
