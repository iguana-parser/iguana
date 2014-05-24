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
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E z
 *     > x E
 *     > E w
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest4 {
	
	private GrammarGraph grammarGraph;
	private Grammar grammar;
	
	private GLLParser parser;
	
	private Nonterminal E = new Nonterminal("E");
	private Character a = Character.from('a');
	private Character w = Character.from('w');
	private Character x = Character.from('x');
	private Character z = Character.from('z');

	@Before
	public void createGrammar() {
		
		grammar = new Grammar();
		
		// E ::= E z
		Rule rule1 = new Rule(E, list(E, z));
		grammar.addRule(rule1);
		
		// E ::=  x E
		Rule rule2 = new Rule(E, list(x, E));
		grammar.addRule(rule2);
		
		// E ::= E w
		Rule rule3 = new Rule(E, list(E, w));
		grammar.addRule(rule3);
		
		// E ::= a
		Rule rule4 = new Rule(E, list(a));
		grammar.addRule(rule4);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E, .E z, x E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, x .E, E w)
		operatorPrecedence.addPrecedencePattern(E, rule2, 1, rule3);
		
		grammar = operatorPrecedence.rewrite(grammar);
		
		grammarGraph = grammar.toGrammarGraph();
	}

	@Test
	public void testAssociativityAndPriority() {
		Input input = Input.fromString("xawz");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 0, 4);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 0, 3);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 0, 2);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(x), 0, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 4, 1, 2);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(w), 2, 1);
		node2.addChild(node3);
		node2.addChild(node7);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(z), 3, 1);
		node1.addChild(node2);
		node1.addChild(node8);
		return node1;
	}

}
