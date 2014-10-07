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
 * E ::= E * E
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest0 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character star = Character.from('*');
	private Character plus = Character.from('+');

	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, star, E));
		builder.addRule(rule1);
		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);

		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E, E * .E, E * E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		
		// (E, E * .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, .E * E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule2);
		
		// (E, E + .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 5).init();
		PackedNode node2 = factory.createPackedNode("E ::= E + E2 .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E + . E2", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("E ::= E + . E2", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("E ::= a .", 0, node5);
		TokenSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		TokenSymbolNode node8 = factory.createTokenNode("+", 1, 1);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node9 = factory.createNonterminalNode("E", 2, 2, 5).init();
		PackedNode node10 = factory.createPackedNode("E2 ::= E2 * E1 .", 4, node9);
		IntermediateNode node11 = factory.createIntermediateNode("E2 ::= E2 * . E1", 2, 4).init();
		PackedNode node12 = factory.createPackedNode("E2 ::= E2 * . E1", 3, node11);
		NonterminalNode node13 = factory.createNonterminalNode("E", 2, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("E2 ::= a .", 2, node13);
		TokenSymbolNode node15 = factory.createTokenNode("a", 2, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		TokenSymbolNode node16 = factory.createTokenNode("*", 3, 1);
		node12.addChild(node13);
		node12.addChild(node16);
		node11.addChild(node12);
		NonterminalNode node17 = factory.createNonterminalNode("E", 1, 4, 5).init();
		PackedNode node18 = factory.createPackedNode("E1 ::= a .", 4, node17);
		TokenSymbolNode node19 = factory.createTokenNode("a", 4, 1);
		node18.addChild(node19);
		node17.addChild(node18);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
