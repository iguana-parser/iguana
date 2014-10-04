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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E [ E ]
 * 	   | E +
 * 	   | E *
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest8 {
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character plus = Character.from('+');
	private Character star = Character.from('*');
	private Character ob = Character.from('[');
	private Character cb = Character.from(']');
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E [ E ]
		Rule rule1 = new Rule(E, list(E, ob, E, cb));
		builder.addRule(rule1);
		
		// E ::= E +
		Rule rule2 = new Rule(E, list(E, plus));
		builder.addRule(rule2);
		
		// E ::= E *
		Rule rule3 = new Rule(E, list(E, star));
		builder.addRule(rule3);
		
		// E ::= E + E
		Rule rule4 = new Rule(E, list(E, plus, E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(a));
		builder.addRule(rule5);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		// (E, .E [ E ], E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule4);
		
		// (E, .E *, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 0, rule4);
		
		// (E, .E +, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule3, 0, rule4);

		operatorPrecedence.addExceptPattern(E, rule1, 0, rule1);
		operatorPrecedence.addExceptPattern(E, rule1, 0, rule2);
		operatorPrecedence.addExceptPattern(E, rule1, 0, rule3);

		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("a+a[a+a]");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("a+a*a+[a+a]");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("a[a][a+a]");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseError());
	}	


	private SPPFNode getSPPF1() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 8).init();
		PackedNode node2 = factory.createPackedNode("E ::= E + E .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("E ::= E + . E", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("E ::= a .", 0, node5);
		TokenSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		TokenSymbolNode node8 = factory.createTokenNode("+", 1, 1);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node9 = factory.createNonterminalNode("E", 0, 2, 8).init();
		PackedNode node10 = factory.createPackedNode("E ::= E2 [ E ] .", 7, node9);
		IntermediateNode node11 = factory.createIntermediateNode("E ::= E2 [ E . ]", 2, 7).init();
		PackedNode node12 = factory.createPackedNode("E ::= E2 [ E . ]", 4, node11);
		IntermediateNode node13 = factory.createIntermediateNode("E ::= E2 [ . E ]", 2, 4).init();
		PackedNode node14 = factory.createPackedNode("E ::= E2 [ . E ]", 3, node13);
		NonterminalNode node15 = factory.createNonterminalNode("E", 2, 2, 3).init();
		PackedNode node16 = factory.createPackedNode("E2 ::= a .", 2, node15);
		TokenSymbolNode node17 = factory.createTokenNode("a", 2, 1);
		node16.addChild(node17);
		node15.addChild(node16);
		TokenSymbolNode node18 = factory.createTokenNode("[", 3, 1);
		node14.addChild(node15);
		node14.addChild(node18);
		node13.addChild(node14);
		NonterminalNode node19 = factory.createNonterminalNode("E", 0, 4, 7).init();
		PackedNode node20 = factory.createPackedNode("E ::= E + E .", 6, node19);
		IntermediateNode node21 = factory.createIntermediateNode("E ::= E + . E", 4, 6).init();
		PackedNode node22 = factory.createPackedNode("E ::= E + . E", 5, node21);
		NonterminalNode node23 = factory.createNonterminalNode("E", 0, 4, 5).init();
		PackedNode node24 = factory.createPackedNode("E ::= a .", 4, node23);
		TokenSymbolNode node25 = factory.createTokenNode("a", 4, 1);
		node24.addChild(node25);
		node23.addChild(node24);
		TokenSymbolNode node26 = factory.createTokenNode("+", 5, 1);
		node22.addChild(node23);
		node22.addChild(node26);
		node21.addChild(node22);
		NonterminalNode node27 = factory.createNonterminalNode("E", 0, 6, 7).init();
		PackedNode node28 = factory.createPackedNode("E ::= a .", 6, node27);
		TokenSymbolNode node29 = factory.createTokenNode("a", 6, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		node20.addChild(node21);
		node20.addChild(node27);
		node19.addChild(node20);
		node12.addChild(node13);
		node12.addChild(node19);
		node11.addChild(node12);
		TokenSymbolNode node30 = factory.createTokenNode("]", 7, 1);
		node10.addChild(node11);
		node10.addChild(node30);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
