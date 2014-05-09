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
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
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

	private GrammarGraph grammarGraph;
	private Grammar grammar;
	
	private GLLParser parser;
	
	private Nonterminal E = new Nonterminal("E");
	private Nonterminal EPlus = new Nonterminal("E+", true);
	private Character a = new Character('a');
	private Character plus = new Character('+');
	
	@Before
	public void createGrammar() {
		
		grammar = new Grammar();
		
		// E ::= E E+
		Rule rule1 = new Rule(E, list(E, EPlus));
		grammar.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		grammar.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		grammar.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(EPlus, list(EPlus, E));
		grammar.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(EPlus, list(E));
		grammar.addRule(rule5);
		
		
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
		
		grammar = operatorPrecedence.rewrite(grammar);
		grammarGraph = grammar.toGrammarGraph();
	}

	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 14);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, plus), 0, 10);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 9);
		IntermediateNode node4 = new IntermediateNode(grammarGraph.getIntermediateNodeId(E, plus), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 0, 1);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node6.addChild(node7);
		ListSymbolNode node8 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 1, 3);
		ListSymbolNode node9 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 1, 2);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node5.addChild(node6);
		node5.addChild(node8);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 3, 1);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 4, 9);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 4, 5);
		TokenSymbolNode node17 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 4, 1);
		node16.addChild(node17);
		ListSymbolNode node18 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 5, 9);
		ListSymbolNode node19 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 5, 8);
		ListSymbolNode node20 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 5, 7);
		ListSymbolNode node21 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 5, 6);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 5, 6);
		TokenSymbolNode node23 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 5, 1);
		node22.addChild(node23);
		node21.addChild(node22);
		NonterminalSymbolNode node24 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 6, 7);
		TokenSymbolNode node25 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 6, 1);
		node24.addChild(node25);
		node20.addChild(node21);
		node20.addChild(node24);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 7, 8);
		TokenSymbolNode node27 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 7, 1);
		node26.addChild(node27);
		node19.addChild(node20);
		node19.addChild(node26);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 8, 9);
		TokenSymbolNode node29 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 8, 1);
		node28.addChild(node29);
		node18.addChild(node19);
		node18.addChild(node28);
		node15.addChild(node16);
		node15.addChild(node18);
		node3.addChild(node4);
		node3.addChild(node15);
		TokenSymbolNode node30 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 9, 1);
		node2.addChild(node3);
		node2.addChild(node30);
		NonterminalSymbolNode node31 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 10, 14);
		NonterminalSymbolNode node32 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 10, 11);
		TokenSymbolNode node33 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 10, 1);
		node32.addChild(node33);
		ListSymbolNode node34 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 11, 14);
		ListSymbolNode node35 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 11, 13);
		ListSymbolNode node36 = new ListSymbolNode(grammarGraph.getNonterminalId(EPlus), 2, 11, 12);
		NonterminalSymbolNode node37 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 11, 12);
		TokenSymbolNode node38 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 11, 1);
		node37.addChild(node38);
		node36.addChild(node37);
		NonterminalSymbolNode node39 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 12, 13);
		TokenSymbolNode node40 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 12, 1);
		node39.addChild(node40);
		node35.addChild(node36);
		node35.addChild(node39);
		NonterminalSymbolNode node41 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 3, 13, 14);
		TokenSymbolNode node42 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 13, 1);
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
