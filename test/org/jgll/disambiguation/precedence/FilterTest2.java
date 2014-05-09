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
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E ^ E	(right)
 *     > E + E	(left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest2 {

	private GrammarGraph grammarGraph;
	private Grammar grammar;
	
	private GLLParser parser;

	private Nonterminal E = new Nonterminal("E");
	private Character a = new Character('a');
	private Character hat = new Character('^');
	private Character plus = new Character('+');
	private Character minus = new Character('-');

	
	@Before
	public void createGrammar() {
		
		// E ::= E ^ E
		Rule rule0 = new Rule(E, list(E, hat, E));
		grammar.addRule(rule0);
		
		// E ::= E + E
		Rule rule1 = new Rule(E, list(E, plus, E));
		grammar.addRule(rule1);
		
		// E ::= E - E
		Rule rule2 = new Rule(E, list(minus, E));
		grammar.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		grammar.addRule(rule3);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// left associative E + E
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		
		// + has higher priority than -
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// right associative E ^ E
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule0);
		
		// ^ has higher priority than -
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule2);
		
		// ^ has higher priority than +
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule1);
		operatorPrecedence.addPrecedencePattern(E, rule0, 2, rule1);
		
		grammar = operatorPrecedence.rewrite(grammar);
		grammarGraph = grammar.toGrammarGraph();
	}

	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("a+a^a^-a+a");
		parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 0, 10);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, plus), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 2, 10);
		IntermediateNode node7 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, hat), 2, 4);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node8.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(hat), 3, 1);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 4, 10);
		IntermediateNode node12 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, hat), 4, 6);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 4, 5);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 4, 1);
		node13.addChild(node14);
		TokenSymbolNode node15 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(hat), 5, 1);
		node12.addChild(node13);
		node12.addChild(node15);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 6, 10);
		TokenSymbolNode node17 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(minus), 6, 1);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 7, 10);
		IntermediateNode node19 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, plus), 7, 9);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 7, 8);
		TokenSymbolNode node21 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 7, 1);
		node20.addChild(node21);
		TokenSymbolNode node22 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 8, 1);
		node19.addChild(node20);
		node19.addChild(node22);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 9, 10);
		TokenSymbolNode node24 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 9, 1);
		node23.addChild(node24);
		node18.addChild(node19);
		node18.addChild(node23);
		node16.addChild(node17);
		node16.addChild(node18);
		node11.addChild(node12);
		node11.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
