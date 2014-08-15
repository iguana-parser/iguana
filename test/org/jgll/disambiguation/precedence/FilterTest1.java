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
 * E ::= E + E
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest1 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');
	private Character a = Character.from('a');

	private Grammar grammar;
	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E + E
		Rule rule1 = new Rule(E, list(E, plus, E));
		builder.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = new Rule(E, list(minus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("a+-a+a");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 6).init();
		PackedNode node2 = factory.createPackedNode("E ::= E2 + E1 .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E2 + . E1", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("E ::= E2 + . E1", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("E ::= a .", 0, node5);
		TokenSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		TokenSymbolNode node8 = factory.createTokenNode("+", 1, 1);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node9 = factory.createNonterminalNode("E", 2, 6).init();
		PackedNode node10 = factory.createPackedNode("E1 ::= - E .", 3, node9);
		TokenSymbolNode node11 = factory.createTokenNode("-", 2, 1);
		NonterminalNode node12 = factory.createNonterminalNode("E", 3, 6).init();
		PackedNode node13 = factory.createPackedNode("E ::= E2 + E1 .", 5, node12);
		IntermediateNode node14 = factory.createIntermediateNode("E ::= E2 + . E1", 3, 5).init();
		PackedNode node15 = factory.createPackedNode("E ::= E2 + . E1", 4, node14);
		NonterminalNode node16 = factory.createNonterminalNode("E", 3, 4).init();
		PackedNode node17 = factory.createPackedNode("E ::= a .", 3, node16);
		TokenSymbolNode node18 = factory.createTokenNode("a", 3, 1);
		node17.addChild(node18);
		node16.addChild(node17);
		TokenSymbolNode node19 = factory.createTokenNode("+", 4, 1);
		node15.addChild(node16);
		node15.addChild(node19);
		node14.addChild(node15);
		NonterminalNode node20 = factory.createNonterminalNode("E", 5, 6).init();
		PackedNode node21 = factory.createPackedNode("E1 ::= a .", 5, node20);
		TokenSymbolNode node22 = factory.createTokenNode("a", 5, 1);
		node21.addChild(node22);
		node20.addChild(node21);
		node13.addChild(node14);
		node13.addChild(node20);
		node12.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node12);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
