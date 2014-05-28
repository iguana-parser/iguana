package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
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

	private GrammarGraph grammarGraph;
	private Grammar grammar;
	
	
	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');
	private Character a = Character.from('a');
	
	@Before
	public void createGrammar() {
		
		grammar = new Grammar();
		
		// E ::= E + E
		Rule rule1 = new Rule(E, list(E, plus, E));
		grammar.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = new Rule(E, list(minus, E));
		grammar.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		grammar.addRule(rule3);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		grammar = operatorPrecedence.rewrite(grammar);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("a+-a+a");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(E, 0, 6);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, plus), 0, 2);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(a, 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = factory.createTokenNode(plus, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(E, 2, 6);
		TokenSymbolNode node7 = factory.createTokenNode(minus, 2, 1);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(E, 3, 6);
		IntermediateNode node9 = factory.createIntermediateNode(list(E, plus), 3, 5);
		NonterminalSymbolNode node10 = factory.createNonterminalNode(E, 3, 4);
		TokenSymbolNode node11 = factory.createTokenNode(a, 3, 1);
		node10.addChild(node11);
		TokenSymbolNode node12 = factory.createTokenNode(plus, 4, 1);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalSymbolNode node13 = factory.createNonterminalNode(E, 5, 6);
		TokenSymbolNode node14 = factory.createTokenNode(a, 5, 1);
		node13.addChild(node14);
		node8.addChild(node9);
		node8.addChild(node13);
		node6.addChild(node7);
		node6.addChild(node8);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
