package org.iguana.disambiguation.precedence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E * E			  (left)
 * 	   > (E + E | E - E)  (left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest6 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character star = Character.from('*');
	private Character minus = Character.from('-');
	private Character plus = Character.from('+');
	private Character a = Character.from('a');

	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E * E
		Rule rule1 = Rule.withHead(E).addSymbols(E, star, E).build();
		builder.addRule(rule1);		
		
		// E ::= E + E
		Rule rule2 = Rule.withHead(E).addSymbols(E, plus, E).build();
		builder.addRule(rule2);

		// E ::= E - E
		Rule rule3 = Rule.withHead(E).addSymbols(E, minus, E).build();
		builder.addRule(rule3);
		
		// E ::= - E
		Rule rule4 = Rule.withHead(E).addSymbols(minus, E).build();
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule5);
		
		
		List<PrecedencePattern> list = new ArrayList<>();
		
		// E * E (left): 		E * . E, E * E
		list.add(PrecedencePattern.from(rule1, 2, rule1));
		
		// E * E > E + E:		E * . E, E + E and . E * E, E + E
		list.add(PrecedencePattern.from(rule1, 2, rule2));
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// E * E > E - E:		E * . E, E - E
		list.add(PrecedencePattern.from(rule1, 2, rule3));
		
		// E * E > - E:			. E * E, - E
		list.add(PrecedencePattern.from(rule1, 0, rule4));
		
		// E * E > E - E		. E * E, E - E
		list.add(PrecedencePattern.from(rule1, 0, rule3));
		list.add(PrecedencePattern.from(rule1, 2, rule3));
		
		// E + E (left):		E + . E, E + E
		list.add(PrecedencePattern.from(rule2, 2, rule2));
		
		// E - E (left):		E - . E, E - E
		list.add(PrecedencePattern.from(rule3, 2, rule3));
		
		// E + E left E - E		E + . E, E - E
		list.add(PrecedencePattern.from(rule2, 2, rule3));
		
		// E - E left E + E		E - . E, E + E 
		list.add(PrecedencePattern.from(rule3, 2, rule2));
		
		// E + E > - E			. E + E, - E
		list.add(PrecedencePattern.from(rule2, 0, rule4));
		
		// E - E > - E			. E - E, - E
		list.add(PrecedencePattern.from(rule3, 0, rule4));
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testInput() {
		Input input = Input.fromString("a+a--a+-a+a-a-a+a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getGrammarGraph())));
	}

	private SPPFNode getSPPF(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 17);
		PackedNode node2 = factory.createPackedNode("E ::= E5 - E4 .", 4, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E5 - . E4", 0, 4);
		PackedNode node4 = factory.createPackedNode("E ::= E5 - . E4", 3, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 5, 0, 3);
		PackedNode node6 = factory.createPackedNode("E5 ::= E5 + E7 .", 2, node5);
		IntermediateNode node7 = factory.createIntermediateNode("E5 ::= E5 + . E7", 0, 2);
		PackedNode node8 = factory.createPackedNode("E5 ::= E5 + . E7", 1, node7);
		NonterminalNode node9 = factory.createNonterminalNode("E", 5, 0, 1);
		PackedNode node10 = factory.createPackedNode("E5 ::= a .", 1, node9);
		TerminalNode node11 = factory.createTerminalNode("a", 0, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		TerminalNode node12 = factory.createTerminalNode("+", 1, 2);
		node8.addChild(node9);
		node8.addChild(node12);
		node7.addChild(node8);
		NonterminalNode node13 = factory.createNonterminalNode("E", 7, 2, 3);
		PackedNode node14 = factory.createPackedNode("E7 ::= a .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("a", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		node6.addChild(node7);
		node6.addChild(node13);
		node5.addChild(node6);
		TerminalNode node16 = factory.createTerminalNode("-", 3, 4);
		node4.addChild(node5);
		node4.addChild(node16);
		node3.addChild(node4);
		NonterminalNode node17 = factory.createNonterminalNode("E", 4, 4, 17);
		PackedNode node18 = factory.createPackedNode("E4 ::= - E .", 5, node17);
		TerminalNode node19 = factory.createTerminalNode("-", 4, 5);
		NonterminalNode node20 = factory.createNonterminalNode("E", 0, 5, 17);
		PackedNode node21 = factory.createPackedNode("E ::= E5 + E3 .", 7, node20);
		IntermediateNode node22 = factory.createIntermediateNode("E ::= E5 + . E3", 5, 7);
		PackedNode node23 = factory.createPackedNode("E ::= E5 + . E3", 6, node22);
		NonterminalNode node24 = factory.createNonterminalNode("E", 5, 5, 6);
		PackedNode node25 = factory.createPackedNode("E5 ::= a .", 6, node24);
		TerminalNode node26 = factory.createTerminalNode("a", 5, 6);
		node25.addChild(node26);
		node24.addChild(node25);
		TerminalNode node27 = factory.createTerminalNode("+", 6, 7);
		node23.addChild(node24);
		node23.addChild(node27);
		node22.addChild(node23);
		NonterminalNode node28 = factory.createNonterminalNode("E", 3, 7, 17);
		PackedNode node29 = factory.createPackedNode("E3 ::= - E .", 8, node28);
		TerminalNode node30 = factory.createTerminalNode("-", 7, 8);
		NonterminalNode node31 = factory.createNonterminalNode("E", 0, 8, 17);
		PackedNode node32 = factory.createPackedNode("E ::= E5 + E3 .", 16, node31);
		IntermediateNode node33 = factory.createIntermediateNode("E ::= E5 + . E3", 8, 16);
		PackedNode node34 = factory.createPackedNode("E ::= E5 + . E3", 15, node33);
		NonterminalNode node35 = factory.createNonterminalNode("E", 5, 8, 15);
		PackedNode node36 = factory.createPackedNode("E5 ::= E5 - E7 .", 14, node35);
		IntermediateNode node37 = factory.createIntermediateNode("E5 ::= E5 - . E7", 8, 14);
		PackedNode node38 = factory.createPackedNode("E5 ::= E5 - . E7", 13, node37);
		NonterminalNode node39 = factory.createNonterminalNode("E", 5, 8, 13);
		PackedNode node40 = factory.createPackedNode("E5 ::= E5 - E7 .", 12, node39);
		IntermediateNode node41 = factory.createIntermediateNode("E5 ::= E5 - . E7", 8, 12);
		PackedNode node42 = factory.createPackedNode("E5 ::= E5 - . E7", 11, node41);
		NonterminalNode node43 = factory.createNonterminalNode("E", 5, 8, 11);
		PackedNode node44 = factory.createPackedNode("E5 ::= E5 + E7 .", 10, node43);
		IntermediateNode node45 = factory.createIntermediateNode("E5 ::= E5 + . E7", 8, 10);
		PackedNode node46 = factory.createPackedNode("E5 ::= E5 + . E7", 9, node45);
		NonterminalNode node47 = factory.createNonterminalNode("E", 5, 8, 9);
		PackedNode node48 = factory.createPackedNode("E5 ::= a .", 9, node47);
		TerminalNode node49 = factory.createTerminalNode("a", 8, 9);
		node48.addChild(node49);
		node47.addChild(node48);
		TerminalNode node50 = factory.createTerminalNode("+", 9, 10);
		node46.addChild(node47);
		node46.addChild(node50);
		node45.addChild(node46);
		NonterminalNode node51 = factory.createNonterminalNode("E", 7, 10, 11);
		PackedNode node52 = factory.createPackedNode("E7 ::= a .", 11, node51);
		TerminalNode node53 = factory.createTerminalNode("a", 10, 11);
		node52.addChild(node53);
		node51.addChild(node52);
		node44.addChild(node45);
		node44.addChild(node51);
		node43.addChild(node44);
		TerminalNode node54 = factory.createTerminalNode("-", 11, 12);
		node42.addChild(node43);
		node42.addChild(node54);
		node41.addChild(node42);
		NonterminalNode node55 = factory.createNonterminalNode("E", 7, 12, 13);
		PackedNode node56 = factory.createPackedNode("E7 ::= a .", 13, node55);
		TerminalNode node57 = factory.createTerminalNode("a", 12, 13);
		node56.addChild(node57);
		node55.addChild(node56);
		node40.addChild(node41);
		node40.addChild(node55);
		node39.addChild(node40);
		TerminalNode node58 = factory.createTerminalNode("-", 13, 14);
		node38.addChild(node39);
		node38.addChild(node58);
		node37.addChild(node38);
		NonterminalNode node59 = factory.createNonterminalNode("E", 7, 14, 15);
		PackedNode node60 = factory.createPackedNode("E7 ::= a .", 15, node59);
		TerminalNode node61 = factory.createTerminalNode("a", 14, 15);
		node60.addChild(node61);
		node59.addChild(node60);
		node36.addChild(node37);
		node36.addChild(node59);
		node35.addChild(node36);
		TerminalNode node62 = factory.createTerminalNode("+", 15, 16);
		node34.addChild(node35);
		node34.addChild(node62);
		node33.addChild(node34);
		NonterminalNode node63 = factory.createNonterminalNode("E", 3, 16, 17);
		PackedNode node64 = factory.createPackedNode("E3 ::= a .", 17, node63);
		TerminalNode node65 = factory.createTerminalNode("a", 16, 17);
		node64.addChild(node65);
		node63.addChild(node64);
		node32.addChild(node33);
		node32.addChild(node63);
		node31.addChild(node32);
		node29.addChild(node30);
		node29.addChild(node31);
		node28.addChild(node29);
		node21.addChild(node22);
		node21.addChild(node28);
		node20.addChild(node21);
		node18.addChild(node19);
		node18.addChild(node20);
		node17.addChild(node18);
		node2.addChild(node3);
		node2.addChild(node17);
		node1.addChild(node2);
		return node1;
	}
	
}
