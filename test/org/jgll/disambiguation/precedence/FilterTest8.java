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
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF1()));
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
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(E, 0, 8);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, plus), 0, 2);
		NonterminalNode node3 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(a, 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = factory.createTokenNode(plus, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(E, 2, 8);
		IntermediateNode node7 = factory.createIntermediateNode(list(E, ob, E), 2, 7);
		IntermediateNode node8 = factory.createIntermediateNode(list(E, ob), 2, 4);
		NonterminalNode node9 = factory.createNonterminalNode(E, 2, 3);
		TokenSymbolNode node10 = factory.createTokenNode(a, 2, 1);
		node9.addChild(node10);
		TokenSymbolNode node11 = factory.createTokenNode(ob, 3, 1);
		node8.addChild(node9);
		node8.addChild(node11);
		NonterminalNode node12 = factory.createNonterminalNode(E, 4, 7);
		IntermediateNode node13 = factory.createIntermediateNode(list(E, plus), 4, 6);
		NonterminalNode node14 = factory.createNonterminalNode(E, 4, 5);
		TokenSymbolNode node15 = factory.createTokenNode(a, 4, 1);
		node14.addChild(node15);
		TokenSymbolNode node16 = factory.createTokenNode(plus, 5, 1);
		node13.addChild(node14);
		node13.addChild(node16);
		NonterminalNode node17 = factory.createNonterminalNode(E, 6, 7);
		TokenSymbolNode node18 = factory.createTokenNode(a, 6, 1);
		node17.addChild(node18);
		node12.addChild(node13);
		node12.addChild(node17);
		node7.addChild(node8);
		node7.addChild(node12);
		TokenSymbolNode node19 = factory.createTokenNode(cb, 7, 1);
		node6.addChild(node7);
		node6.addChild(node19);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
