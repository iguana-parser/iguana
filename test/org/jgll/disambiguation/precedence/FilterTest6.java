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
 * E ::= E * E			  (left)
 * 	   > (E + E | E - E)  (left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest6 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character star = Character.from('*');
	private Character minus = Character.from('-');
	private Character plus = Character.from('+');
	private Character a = Character.from('a');

	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E * E
		Rule rule1 = new Rule(E, list(E, star, E));
		builder.addRule(rule1);		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);

		// E ::= E - E
		Rule rule3 = new Rule(E, list(E, minus, E));
		builder.addRule(rule3);
		
		// E ::= - E
		Rule rule4 = new Rule(E, list(minus, E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(a));
		builder.addRule(rule5);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// E * E (left): 		E * . E, E * E
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		
		// E * E > E + E:		E * . E, E + E and . E * E, E + E
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule2);
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// E * E > E - E:		E * . E, E - E
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule3);
		
		// E * E > - E:			. E * E, - E
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule4);
		
		// E * E > E - E		. E * E, E - E
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule3);
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule3);
		
		// E + E (left):		E + . E, E + E
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		// E - E (left):		E - . E, E - E
		operatorPrecedence.addPrecedencePattern(E, rule3, 2, rule3);
		
		// E + E left E - E		E + . E, E - E
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule3);
		
		// E - E left E + E		E - . E, E + E 
		operatorPrecedence.addPrecedencePattern(E, rule3, 2, rule2);
		
		// E + E > - E			. E + E, - E
		operatorPrecedence.addPrecedencePattern(E, rule2, 0, rule4);
		
		// E - E > - E			. E - E, - E
		operatorPrecedence.addPrecedencePattern(E, rule3, 0, rule4);
		
		grammar = operatorPrecedence.transform(builder.build());
	}
	
	@Test
	public void testInput() {
		Input input = Input.fromString("a+a--a+-a+a-a-a+a");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}

	private SPPFNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(E, 0, 17);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, minus), 0, 4);
		NonterminalNode node3 = factory.createNonterminalNode(E, 0, 3);
		IntermediateNode node4 = factory.createIntermediateNode(list(E, plus), 0, 2);
		NonterminalNode node5 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node6 = factory.createTokenNode(a, 0, 1);
		node5.addChild(node6);
		TokenSymbolNode node7 = factory.createTokenNode(plus, 1, 1);
		node4.addChild(node5);
		node4.addChild(node7);
		NonterminalNode node8 = factory.createNonterminalNode(E, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(a, 2, 1);
		node8.addChild(node9);
		node3.addChild(node4);
		node3.addChild(node8);
		TokenSymbolNode node10 = factory.createTokenNode(minus, 3, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalNode node11 = factory.createNonterminalNode(E, 4, 17);
		TokenSymbolNode node12 = factory.createTokenNode(minus, 4, 1);
		NonterminalNode node13 = factory.createNonterminalNode(E, 5, 17);
		IntermediateNode node14 = factory.createIntermediateNode(list(E, plus), 5, 7);
		NonterminalNode node15 = factory.createNonterminalNode(E, 5, 6);
		TokenSymbolNode node16 = factory.createTokenNode(a, 5, 1);
		node15.addChild(node16);
		TokenSymbolNode node17 = factory.createTokenNode(plus, 6, 1);
		node14.addChild(node15);
		node14.addChild(node17);
		NonterminalNode node18 = factory.createNonterminalNode(E, 7, 17);
		TokenSymbolNode node19 = factory.createTokenNode(minus, 7, 1);
		NonterminalNode node20 = factory.createNonterminalNode(E, 8, 17);
		IntermediateNode node21 = factory.createIntermediateNode(list(E, plus), 8, 16);
		NonterminalNode node22 = factory.createNonterminalNode(E, 8, 15);
		IntermediateNode node23 = factory.createIntermediateNode(list(E, minus), 8, 14);
		NonterminalNode node24 = factory.createNonterminalNode(E, 8, 13);
		IntermediateNode node25 = factory.createIntermediateNode(list(E, minus), 8, 12);
		NonterminalNode node26 = factory.createNonterminalNode(E, 8, 11);
		IntermediateNode node27 = factory.createIntermediateNode(list(E, plus), 8, 10);
		NonterminalNode node28 = factory.createNonterminalNode(E, 8, 9);
		TokenSymbolNode node29 = factory.createTokenNode(a, 8, 1);
		node28.addChild(node29);
		TokenSymbolNode node30 = factory.createTokenNode(plus, 9, 1);
		node27.addChild(node28);
		node27.addChild(node30);
		NonterminalNode node31 = factory.createNonterminalNode(E, 10, 11);
		TokenSymbolNode node32 = factory.createTokenNode(a, 10, 1);
		node31.addChild(node32);
		node26.addChild(node27);
		node26.addChild(node31);
		TokenSymbolNode node33 = factory.createTokenNode(minus, 11, 1);
		node25.addChild(node26);
		node25.addChild(node33);
		NonterminalNode node34 = factory.createNonterminalNode(E, 12, 13);
		TokenSymbolNode node35 = factory.createTokenNode(a, 12, 1);
		node34.addChild(node35);
		node24.addChild(node25);
		node24.addChild(node34);
		TokenSymbolNode node36 = factory.createTokenNode(minus, 13, 1);
		node23.addChild(node24);
		node23.addChild(node36);
		NonterminalNode node37 = factory.createNonterminalNode(E, 14, 15);
		TokenSymbolNode node38 = factory.createTokenNode(a, 14, 1);
		node37.addChild(node38);
		node22.addChild(node23);
		node22.addChild(node37);
		TokenSymbolNode node39 = factory.createTokenNode(plus, 15, 1);
		node21.addChild(node22);
		node21.addChild(node39);
		NonterminalNode node40 = factory.createNonterminalNode(E, 16, 17);
		TokenSymbolNode node41 = factory.createTokenNode(a, 16, 1);
		node40.addChild(node41);
		node20.addChild(node21);
		node20.addChild(node40);
		node18.addChild(node19);
		node18.addChild(node20);
		node13.addChild(node14);
		node13.addChild(node18);
		node11.addChild(node12);
		node11.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
