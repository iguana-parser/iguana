package org.jgll.disambiguation.precedence;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
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
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E Y    (none)
 * 	   > E ; E  (right)
 * 	   > - E
 *     | a
 * 
 * Y ::= X
 * 
 * X ::= X , E
 *     | , E
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest11 {
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal X = Nonterminal.withName("X");
	private Nonterminal Y = Nonterminal.withName("Y");
	private Character a = Character.from('a');
	private Character comma = Character.from(',');
	private Character semicolon = Character.from(';');
	private Keyword min = Keyword.from("-");
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E Y
		Rule rule1 = Rule.builder(E).addSymbols(E, Y).build();
		builder.addRule(rule1);
		
		// E ::= E ; E
		Rule rule2 = Rule.builder(E).addSymbols(E, semicolon, E).build();
		builder.addRule(rule2);
		
		// E ::= - E
		Rule rule3 = Rule.builder(E).addSymbols(min, E).build();
		builder.addRule(rule3);
		
		// E ::= a
		Rule rule4 = Rule.builder(E).addSymbols(a).build();
		builder.addRule(rule4);
		
		// Y ::= X
		Rule rule5 = Rule.builder(Y).addSymbols(X).build();
		builder.addRule(rule5);
		
		// X ::= X , E
		Rule rule6 = Rule.builder(X).addSymbols(X, comma, E).build();
		builder.addRule(rule6);
		
		// X ::= , E
		Rule rule7 = Rule.builder(X).addSymbols(comma, E).build();
		builder.addRule(rule7);
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();

		// (E, .E Y, E ";" E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, E .Y, E ";" E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E, .E Y, - E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule3);		
		
		// (E, .E ";" E, - E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 0, rule3);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("a,-a;a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	public SPPFNode getSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 6);
		PackedNode node2 = factory.createPackedNode("E ::= E1 Y1 .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("E", 1, 0, 1);
		PackedNode node4 = factory.createPackedNode("E1 ::= a .", 0, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("Y", 1, 1, 6);
		PackedNode node7 = factory.createPackedNode("Y1 ::= X1 .", 1, node6);
		NonterminalNode node8 = factory.createNonterminalNode("X", 1, 1, 6);
		PackedNode node9 = factory.createPackedNode("X1 ::= , E2 .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode(",", 1, 2);
		NonterminalNode node11 = factory.createNonterminalNode("E", 2, 2, 6);
		PackedNode node12 = factory.createPackedNode("E2 ::= - E .", 3, node11);
		TerminalNode node13 = factory.createTerminalNode("-", 2, 3);
		NonterminalNode node14 = factory.createNonterminalNode("E", 0, 3, 6);
		PackedNode node15 = factory.createPackedNode("E ::= E3 ; E .", 5, node14);
		IntermediateNode node16 = factory.createIntermediateNode("E ::= E3 ; . E", 3, 5);
		PackedNode node17 = factory.createPackedNode("E ::= E3 ; . E", 4, node16);
		NonterminalNode node18 = factory.createNonterminalNode("E", 3, 3, 4);
		PackedNode node19 = factory.createPackedNode("E3 ::= a .", 3, node18);
		TerminalNode node20 = factory.createTerminalNode("a", 3, 4);
		node19.addChild(node20);
		node18.addChild(node19);
		TerminalNode node21 = factory.createTerminalNode(";", 4, 5);
		node17.addChild(node18);
		node17.addChild(node21);
		node16.addChild(node17);
		NonterminalNode node22 = factory.createNonterminalNode("E", 0, 5, 6);
		PackedNode node23 = factory.createPackedNode("E ::= a .", 5, node22);
		TerminalNode node24 = factory.createTerminalNode("a", 5, 6);
		node23.addChild(node24);
		node22.addChild(node23);
		node15.addChild(node16);
		node15.addChild(node22);
		node14.addChild(node15);
		node12.addChild(node13);
		node12.addChild(node14);
		node11.addChild(node12);
		node9.addChild(node10);
		node9.addChild(node11);
		node8.addChild(node9);
		node7.addChild(node8);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		node1.addChild(node2);
		return node1;
	}

}
