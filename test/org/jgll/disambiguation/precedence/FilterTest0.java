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
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.ToJavaCode;
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
public class FilterTest0 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character star = Character.from('*');
	private Character plus = Character.from('+');

	private Grammar grammar;
	private GrammarGraph grammarGraph;

	@Before
	public void init() {
		
		grammar = new Grammar();
		
		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, star, E));
		grammar.addRule(rule1);
		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		grammar.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		grammar.addRule(rule3);

		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E, E * .E, E * E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		
		// (E, E * .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, .E * E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule2);
		
		// (E, E + .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		grammar = operatorPrecedence.rewrite(grammar);
		grammarGraph = grammar.toGrammarGraph();
				
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "E");
		System.out.println(ToJavaCode.toJavaCode((NonterminalSymbolNode) result.asParseSuccess().getSPPFNode(), grammarGraph));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, plus), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 2, 2, 5);
		IntermediateNode node7 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, star), 2, 4);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 2, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node8.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(star), 3, 1);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 1, 4, 5);
		TokenSymbolNode node12 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 4, 1);
		node11.addChild(node12);
		node6.addChild(node7);
		node6.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
