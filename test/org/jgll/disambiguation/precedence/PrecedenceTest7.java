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
 * E ::= EPlus E     (non-assoc)
 *     > E + E	  (left)
 *     | a
 * 
 * EPlus ::= EPlus E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest7 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal EPlus = new Nonterminal.Builder("EPlus").setEbnfList(true).build();
	private Character a = Character.from('a');
	private Character plus = Character.from('+');

	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= EPlus E
		Rule rule1 = Rule.builder(E).addSymbols(EPlus, E).build();
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = Rule.builder(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.builder(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		// EPlus ::= EPlus E
		Rule rule4 = Rule.builder(EPlus).addSymbols(EPlus, E).build();
		builder.addRule(rule4);
		
		// EPlus ::= E
		Rule rule5 = Rule.builder(EPlus).addSymbols(E).build();
		builder.addRule(rule5);
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E ::= .EPlus E, EPlus E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= EPlus .E, EPlus E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .EPlus E, E + E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= EPlus .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E + .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule2);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule2);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aaa+aaaa+aaaa");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	private NonterminalNode getSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 13);
		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 9, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 9);
		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 8, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 8);
		PackedNode node6 = factory.createPackedNode("E ::= E + E2 .", 4, node5);
		IntermediateNode node7 = factory.createIntermediateNode("E ::= E + . E2", 0, 4);
		PackedNode node8 = factory.createPackedNode("E ::= E + . E2", 3, node7);
		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 0, 3);
		PackedNode node10 = factory.createPackedNode("E ::= EPlus1 E1 .", 2, node9);
		ListSymbolNode node11 = factory.createListNode("EPlus", 1, 0, 2);
		PackedNode node12 = factory.createPackedNode("EPlus1 ::= EPlus1 E3 .", 1, node11);
		ListSymbolNode node13 = factory.createListNode("EPlus", 1, 0, 1);
		PackedNode node14 = factory.createPackedNode("EPlus1 ::= E3 .", 0, node13);
		NonterminalNode node15 = factory.createNonterminalNode("E", 3, 0, 1);
		PackedNode node16 = factory.createPackedNode("E3 ::= a .", 0, node15);
		TerminalNode node17 = factory.createTerminalNode("a", 0, 1);
		node16.addChild(node17);
		node15.addChild(node16);
		node14.addChild(node15);
		node13.addChild(node14);
		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 1, 2);
		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 1, node18);
		TerminalNode node20 = factory.createTerminalNode("a", 1, 2);
		node19.addChild(node20);
		node18.addChild(node19);
		node12.addChild(node13);
		node12.addChild(node18);
		node11.addChild(node12);
		NonterminalNode node21 = factory.createNonterminalNode("E", 1, 2, 3);
		PackedNode node22 = factory.createPackedNode("E1 ::= a .", 2, node21);
		TerminalNode node23 = factory.createTerminalNode("a", 2, 3);
		node22.addChild(node23);
		node21.addChild(node22);
		node10.addChild(node11);
		node10.addChild(node21);
		node9.addChild(node10);
		TerminalNode node24 = factory.createTerminalNode("+", 3, 4);
		node8.addChild(node9);
		node8.addChild(node24);
		node7.addChild(node8);
		NonterminalNode node25 = factory.createNonterminalNode("E", 2, 4, 8);
		PackedNode node26 = factory.createPackedNode("E2 ::= EPlus2 E1 .", 7, node25);
		ListSymbolNode node27 = factory.createListNode("EPlus", 2, 4, 7);
		PackedNode node28 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 6, node27);
		ListSymbolNode node29 = factory.createListNode("EPlus", 2, 4, 6);
		PackedNode node30 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 5, node29);
		ListSymbolNode node31 = factory.createListNode("EPlus", 2, 4, 5);
		PackedNode node32 = factory.createPackedNode("EPlus2 ::= E3 .", 4, node31);
		NonterminalNode node33 = factory.createNonterminalNode("E", 3, 4, 5);
		PackedNode node34 = factory.createPackedNode("E3 ::= a .", 4, node33);
		TerminalNode node35 = factory.createTerminalNode("a", 4, 5);
		node34.addChild(node35);
		node33.addChild(node34);
		node32.addChild(node33);
		node31.addChild(node32);
		NonterminalNode node36 = factory.createNonterminalNode("E", 3, 5, 6);
		PackedNode node37 = factory.createPackedNode("E3 ::= a .", 5, node36);
		TerminalNode node38 = factory.createTerminalNode("a", 5, 6);
		node37.addChild(node38);
		node36.addChild(node37);
		node30.addChild(node31);
		node30.addChild(node36);
		node29.addChild(node30);
		NonterminalNode node39 = factory.createNonterminalNode("E", 3, 6, 7);
		PackedNode node40 = factory.createPackedNode("E3 ::= a .", 6, node39);
		TerminalNode node41 = factory.createTerminalNode("a", 6, 7);
		node40.addChild(node41);
		node39.addChild(node40);
		node28.addChild(node29);
		node28.addChild(node39);
		node27.addChild(node28);
		NonterminalNode node42 = factory.createNonterminalNode("E", 1, 7, 8);
		PackedNode node43 = factory.createPackedNode("E1 ::= a .", 7, node42);
		TerminalNode node44 = factory.createTerminalNode("a", 7, 8);
		node43.addChild(node44);
		node42.addChild(node43);
		node26.addChild(node27);
		node26.addChild(node42);
		node25.addChild(node26);
		node6.addChild(node7);
		node6.addChild(node25);
		node5.addChild(node6);
		TerminalNode node45 = factory.createTerminalNode("+", 8, 9);
		node4.addChild(node5);
		node4.addChild(node45);
		node3.addChild(node4);
		NonterminalNode node46 = factory.createNonterminalNode("E", 2, 9, 13);
		PackedNode node47 = factory.createPackedNode("E2 ::= EPlus2 E1 .", 12, node46);
		ListSymbolNode node48 = factory.createListNode("EPlus", 2, 9, 12);
		PackedNode node49 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 11, node48);
		ListSymbolNode node50 = factory.createListNode("EPlus", 2, 9, 11);
		PackedNode node51 = factory.createPackedNode("EPlus2 ::= EPlus2 E3 .", 10, node50);
		ListSymbolNode node52 = factory.createListNode("EPlus", 2, 9, 10);
		PackedNode node53 = factory.createPackedNode("EPlus2 ::= E3 .", 9, node52);
		NonterminalNode node54 = factory.createNonterminalNode("E", 3, 9, 10);
		PackedNode node55 = factory.createPackedNode("E3 ::= a .", 9, node54);
		TerminalNode node56 = factory.createTerminalNode("a", 9, 10);
		node55.addChild(node56);
		node54.addChild(node55);
		node53.addChild(node54);
		node52.addChild(node53);
		NonterminalNode node57 = factory.createNonterminalNode("E", 3, 10, 11);
		PackedNode node58 = factory.createPackedNode("E3 ::= a .", 10, node57);
		TerminalNode node59 = factory.createTerminalNode("a", 10, 11);
		node58.addChild(node59);
		node57.addChild(node58);
		node51.addChild(node52);
		node51.addChild(node57);
		node50.addChild(node51);
		NonterminalNode node60 = factory.createNonterminalNode("E", 3, 11, 12);
		PackedNode node61 = factory.createPackedNode("E3 ::= a .", 11, node60);
		TerminalNode node62 = factory.createTerminalNode("a", 11, 12);
		node61.addChild(node62);
		node60.addChild(node61);
		node49.addChild(node50);
		node49.addChild(node60);
		node48.addChild(node49);
		NonterminalNode node63 = factory.createNonterminalNode("E", 1, 12, 13);
		PackedNode node64 = factory.createPackedNode("E1 ::= a .", 12, node63);
		TerminalNode node65 = factory.createTerminalNode("a", 12, 13);
		node64.addChild(node65);
		node63.addChild(node64);
		node47.addChild(node48);
		node47.addChild(node63);
		node46.addChild(node47);
		node2.addChild(node3);
		node2.addChild(node46);
		node1.addChild(node2);
		return node1;
	}

}
