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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
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

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character hat = Character.from('^');
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');

	private Grammar grammar;

	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E ^ E
		Rule rule0 = new Rule(E, list(E, hat, E));
		builder.addRule(rule0);
		
		// E ::= E + E
		Rule rule1 = new Rule(E, list(E, plus, E));
		builder.addRule(rule1);
		
		// E ::= E - E
		Rule rule2 = new Rule(E, list(minus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
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
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void test() {
		Input input = Input.fromString("a+a^a^-a+a");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(E, 0, 10);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, plus), 0, 2);
		NonterminalNode node3 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(a, 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = factory.createTokenNode(plus, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(E, 2, 10);
		IntermediateNode node7 = factory.createIntermediateNode(list(E, hat), 2, 4);
		NonterminalNode node8 = factory.createNonterminalNode(E, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(a, 2, 1);
		node8.addChild(node9);
		TokenSymbolNode node10 = factory.createTokenNode(hat, 3, 1);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalNode node11 = factory.createNonterminalNode(E, 4, 10);
		IntermediateNode node12 = factory.createIntermediateNode(list(E, hat), 4, 6);
		NonterminalNode node13 = factory.createNonterminalNode(E, 4, 5);
		TokenSymbolNode node14 = factory.createTokenNode(a, 4, 1);
		node13.addChild(node14);
		TokenSymbolNode node15 = factory.createTokenNode(hat, 5, 1);
		node12.addChild(node13);
		node12.addChild(node15);
		NonterminalNode node16 = factory.createNonterminalNode(E, 6, 10);
		TokenSymbolNode node17 = factory.createTokenNode(minus, 6, 1);
		NonterminalNode node18 = factory.createNonterminalNode(E, 7, 10);
		IntermediateNode node19 = factory.createIntermediateNode(list(E, plus), 7, 9);
		NonterminalNode node20 = factory.createNonterminalNode(E, 7, 8);
		TokenSymbolNode node21 = factory.createTokenNode(a, 7, 1);
		node20.addChild(node21);
		TokenSymbolNode node22 = factory.createTokenNode(plus, 8, 1);
		node19.addChild(node20);
		node19.addChild(node22);
		NonterminalNode node23 = factory.createNonterminalNode(E, 9, 10);
		TokenSymbolNode node24 = factory.createTokenNode(a, 9, 1);
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
