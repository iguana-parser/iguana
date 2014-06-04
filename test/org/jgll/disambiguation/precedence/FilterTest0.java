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
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(E, 0, 5);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, plus), 0, 2);
		NonterminalSymbolNode node3 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(a, 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = factory.createTokenNode(plus, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = factory.createNonterminalNode(E, 2, 5);
		IntermediateNode node7 = factory.createIntermediateNode(list(E, star), 2, 4);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(E, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(a, 2, 1);
		node8.addChild(node9);
		TokenSymbolNode node10 = factory.createTokenNode(star, 3, 1);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = factory.createNonterminalNode(E, 4, 5);
		TokenSymbolNode node12 = factory.createTokenNode(a, 4, 1);
		node11.addChild(node12);
		node6.addChild(node7);
		node6.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
