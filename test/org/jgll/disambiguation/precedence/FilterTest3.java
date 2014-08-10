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
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E E+    (non-assoc)
 *     > E + E	 (left)
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest3 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal EPlus = new Nonterminal.Builder("E+").setEbnfList(true).build();
	private Character a = Character.from('a');
	private Character plus = Character.from('+');

	private Grammar grammar;
	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E E+
		Rule rule1 = new Rule(E, list(E, EPlus));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(EPlus, list(EPlus, E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(EPlus, list(E));
		builder.addRule(rule5);
		
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E ::= .E E+, E E+)
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E + .E, E + E)
		operatorPrecedence.addPrecedencePattern(E, rule2, 2, rule2);
		
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule4, 1, rule2);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule1);
		operatorPrecedence.addExceptPattern(EPlus, rule5, 0, rule2);
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private NonterminalNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(E, 0, 14);
		IntermediateNode node2 = factory.createIntermediateNode(list(E, plus), 0, 10);
		NonterminalNode node3 = factory.createNonterminalNode(E, 0, 9);
		IntermediateNode node4 = factory.createIntermediateNode(list(E, plus), 0, 4);
		NonterminalNode node5 = factory.createNonterminalNode(E, 0, 3);
		NonterminalNode node6 = factory.createNonterminalNode(E, 0, 1);
		TokenSymbolNode node7 = factory.createTokenNode(a, 0, 1);
		node6.addChild(node7);
		ListSymbolNode node8 = factory.createListNode(EPlus, 1, 3);
		ListSymbolNode node9 = factory.createListNode(EPlus, 1, 2);
		NonterminalNode node10 = factory.createNonterminalNode(E, 1, 2);
		TokenSymbolNode node11 = factory.createTokenNode(a, 1, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalNode node12 = factory.createNonterminalNode(E, 2, 3);
		TokenSymbolNode node13 = factory.createTokenNode(a, 2, 1);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node5.addChild(node6);
		node5.addChild(node8);
		TokenSymbolNode node14 = factory.createTokenNode(plus, 3, 1);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalNode node15 = factory.createNonterminalNode(E, 4, 9);
		NonterminalNode node16 = factory.createNonterminalNode(E, 4, 5);
		TokenSymbolNode node17 = factory.createTokenNode(a, 4, 1);
		node16.addChild(node17);
		ListSymbolNode node18 = factory.createListNode(EPlus, 5, 9);
		ListSymbolNode node19 = factory.createListNode(EPlus, 5, 8);
		ListSymbolNode node20 = factory.createListNode(EPlus, 5, 7);
		ListSymbolNode node21 = factory.createListNode(EPlus, 5, 6);
		NonterminalNode node22 = factory.createNonterminalNode(E, 5, 6);
		TokenSymbolNode node23 = factory.createTokenNode(a, 5, 1);
		node22.addChild(node23);
		node21.addChild(node22);
		NonterminalNode node24 = factory.createNonterminalNode(E, 6, 7);
		TokenSymbolNode node25 = factory.createTokenNode(a, 6, 1);
		node24.addChild(node25);
		node20.addChild(node21);
		node20.addChild(node24);
		NonterminalNode node26 = factory.createNonterminalNode(E, 7, 8);
		TokenSymbolNode node27 = factory.createTokenNode(a, 7, 1);
		node26.addChild(node27);
		node19.addChild(node20);
		node19.addChild(node26);
		NonterminalNode node28 = factory.createNonterminalNode(E, 8, 9);
		TokenSymbolNode node29 = factory.createTokenNode(a, 8, 1);
		node28.addChild(node29);
		node18.addChild(node19);
		node18.addChild(node28);
		node15.addChild(node16);
		node15.addChild(node18);
		node3.addChild(node4);
		node3.addChild(node15);
		TokenSymbolNode node30 = factory.createTokenNode(plus, 9, 1);
		node2.addChild(node3);
		node2.addChild(node30);
		NonterminalNode node31 = factory.createNonterminalNode(E, 10, 14);
		NonterminalNode node32 = factory.createNonterminalNode(E, 10, 11);
		TokenSymbolNode node33 = factory.createTokenNode(a, 10, 1);
		node32.addChild(node33);
		ListSymbolNode node34 = factory.createListNode(EPlus, 11, 14);
		ListSymbolNode node35 = factory.createListNode(EPlus, 11, 13);
		ListSymbolNode node36 = factory.createListNode(EPlus, 11, 12);
		NonterminalNode node37 = factory.createNonterminalNode(E, 11, 12);
		TokenSymbolNode node38 = factory.createTokenNode(a, 11, 1);
		node37.addChild(node38);
		node36.addChild(node37);
		NonterminalNode node39 = factory.createNonterminalNode(E, 12, 13);
		TokenSymbolNode node40 = factory.createTokenNode(a, 12, 1);
		node39.addChild(node40);
		node35.addChild(node36);
		node35.addChild(node39);
		NonterminalNode node41 = factory.createNonterminalNode(E, 13, 14);
		TokenSymbolNode node42 = factory.createTokenNode(a, 13, 1);
		node41.addChild(node42);
		node34.addChild(node35);
		node34.addChild(node41);
		node31.addChild(node32);
		node31.addChild(node34);
		node1.addChild(node2);
		node1.addChild(node31);
		return node1;
	}

}
