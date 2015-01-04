package org.jgll.disambiguation.precedence;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
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
import org.jgll.util.Configuration;
import org.jgll.util.Input;
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
		Rule rule1 = Rule.builder(E).addSymbols(E, EPlus).build();
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = Rule.builder(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.builder(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = Rule.builder(EPlus).addSymbols(EPlus, E).build();
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = Rule.builder(EPlus).addSymbols(E).build();
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
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, "E");
		assertTrue(result.isParseSuccess());
        assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	private NonterminalNode getSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 14);
		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 10, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 10);
		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 9, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 9);
		PackedNode node6 = factory.createPackedNode("E ::= E + E2 .", 4, node5);
		IntermediateNode node7 = factory.createIntermediateNode("E ::= E + . E2", 0, 4);
		PackedNode node8 = factory.createPackedNode("E ::= E + . E2", 3, node7);
		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 0, 3);
		PackedNode node10 = factory.createPackedNode("E ::= E1 E+1 .", 1, node9);
		NonterminalNode node11 = factory.createNonterminalNode("E", 1, 0, 1);
		PackedNode node12 = factory.createPackedNode("E1 ::= a .", 0, node11);
		TerminalNode node13 = factory.createTerminalNode("a", 0, 1);
		node12.addChild(node13);
		node11.addChild(node12);
		ListSymbolNode node14 = factory.createListNode("E+", 1, 1, 3);
		PackedNode node15 = factory.createPackedNode("E+1 ::= E+ E3 .", 2, node14);
		ListSymbolNode node16 = factory.createListNode("E+", 0, 1, 2);
		PackedNode node17 = factory.createPackedNode("E+ ::= E3 .", 1, node16);
		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 1, 2);
		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 1, node18);
		TerminalNode node20 = factory.createTerminalNode("a", 1, 2);
		node19.addChild(node20);
		node18.addChild(node19);
		node17.addChild(node18);
		node16.addChild(node17);
		NonterminalNode node21 = factory.createNonterminalNode("E", 3, 2, 3);
		PackedNode node22 = factory.createPackedNode("E3 ::= a .", 2, node21);
		TerminalNode node23 = factory.createTerminalNode("a", 2, 3);
		node22.addChild(node23);
		node21.addChild(node22);
		node15.addChild(node16);
		node15.addChild(node21);
		node14.addChild(node15);
		node10.addChild(node11);
		node10.addChild(node14);
		node9.addChild(node10);
		TerminalNode node24 = factory.createTerminalNode("+", 3, 4);
		node8.addChild(node9);
		node8.addChild(node24);
		node7.addChild(node8);
		NonterminalNode node25 = factory.createNonterminalNode("E", 2, 4, 9);
		PackedNode node26 = factory.createPackedNode("E2 ::= E1 E+2 .", 5, node25);
		NonterminalNode node27 = factory.createNonterminalNode("E", 1, 4, 5);
		PackedNode node28 = factory.createPackedNode("E1 ::= a .", 4, node27);
		TerminalNode node29 = factory.createTerminalNode("a", 4, 5);
		node28.addChild(node29);
		node27.addChild(node28);
		ListSymbolNode node30 = factory.createListNode("E+", 2, 5, 9);
		PackedNode node31 = factory.createPackedNode("E+2 ::= E+ E3 .", 8, node30);
		ListSymbolNode node32 = factory.createListNode("E+", 0, 5, 8);
		PackedNode node33 = factory.createPackedNode("E+ ::= E+ E3 .", 7, node32);
		ListSymbolNode node34 = factory.createListNode("E+", 0, 5, 7);
		PackedNode node35 = factory.createPackedNode("E+ ::= E+ E3 .", 6, node34);
		ListSymbolNode node36 = factory.createListNode("E+", 0, 5, 6);
		PackedNode node37 = factory.createPackedNode("E+ ::= E3 .", 5, node36);
		NonterminalNode node38 = factory.createNonterminalNode("E", 3, 5, 6);
		PackedNode node39 = factory.createPackedNode("E3 ::= a .", 5, node38);
		TerminalNode node40 = factory.createTerminalNode("a", 5, 6);
		node39.addChild(node40);
		node38.addChild(node39);
		node37.addChild(node38);
		node36.addChild(node37);
		NonterminalNode node41 = factory.createNonterminalNode("E", 3, 6, 7);
		PackedNode node42 = factory.createPackedNode("E3 ::= a .", 6, node41);
		TerminalNode node43 = factory.createTerminalNode("a", 6, 7);
		node42.addChild(node43);
		node41.addChild(node42);
		node35.addChild(node36);
		node35.addChild(node41);
		node34.addChild(node35);
		NonterminalNode node44 = factory.createNonterminalNode("E", 3, 7, 8);
		PackedNode node45 = factory.createPackedNode("E3 ::= a .", 7, node44);
		TerminalNode node46 = factory.createTerminalNode("a", 7, 8);
		node45.addChild(node46);
		node44.addChild(node45);
		node33.addChild(node34);
		node33.addChild(node44);
		node32.addChild(node33);
		NonterminalNode node47 = factory.createNonterminalNode("E", 3, 8, 9);
		PackedNode node48 = factory.createPackedNode("E3 ::= a .", 8, node47);
		TerminalNode node49 = factory.createTerminalNode("a", 8, 9);
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
		TerminalNode node50 = factory.createTerminalNode("+", 9, 10);
		node4.addChild(node5);
		node4.addChild(node50);
		node3.addChild(node4);
		NonterminalNode node51 = factory.createNonterminalNode("E", 2, 10, 14);
		PackedNode node52 = factory.createPackedNode("E2 ::= E1 E+2 .", 11, node51);
		NonterminalNode node53 = factory.createNonterminalNode("E", 1, 10, 11);
		PackedNode node54 = factory.createPackedNode("E1 ::= a .", 10, node53);
		TerminalNode node55 = factory.createTerminalNode("a", 10, 11);
		node54.addChild(node55);
		node53.addChild(node54);
		ListSymbolNode node56 = factory.createListNode("E+", 2, 11, 14);
		PackedNode node57 = factory.createPackedNode("E+2 ::= E+ E3 .", 13, node56);
		ListSymbolNode node58 = factory.createListNode("E+", 0, 11, 13);
		PackedNode node59 = factory.createPackedNode("E+ ::= E+ E3 .", 12, node58);
		ListSymbolNode node60 = factory.createListNode("E+", 0, 11, 12);
		PackedNode node61 = factory.createPackedNode("E+ ::= E3 .", 11, node60);
		NonterminalNode node62 = factory.createNonterminalNode("E", 3, 11, 12);
		PackedNode node63 = factory.createPackedNode("E3 ::= a .", 11, node62);
		TerminalNode node64 = factory.createTerminalNode("a", 11, 12);
		node63.addChild(node64);
		node62.addChild(node63);
		node61.addChild(node62);
		node60.addChild(node61);
		NonterminalNode node65 = factory.createNonterminalNode("E", 3, 12, 13);
		PackedNode node66 = factory.createPackedNode("E3 ::= a .", 12, node65);
		TerminalNode node67 = factory.createTerminalNode("a", 12, 13);
		node66.addChild(node67);
		node65.addChild(node66);
		node59.addChild(node60);
		node59.addChild(node65);
		node58.addChild(node59);
		NonterminalNode node68 = factory.createNonterminalNode("E", 3, 13, 14);
		PackedNode node69 = factory.createPackedNode("E3 ::= a .", 13, node68);
		TerminalNode node70 = factory.createTerminalNode("a", 13, 14);
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
