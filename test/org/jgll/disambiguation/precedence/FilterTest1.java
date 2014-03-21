package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
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
 * E ::= E + E
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest1 {

	private Grammar grammar;
	private GLLParser parser;

	private Nonterminal E = new Nonterminal("E");
	private Character plus = new Character('+');
	private Character minus = new Character('-');
	private Character a = new Character('a');
	
	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);
		
		// E ::= E + E
		Rule rule1 = new Rule(E, list(E, plus, E));
		builder.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = new Rule(E, list(minus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParser1() throws ParseError {
		Input input = Input.fromString("a+-a+a");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		assertTrue(sppf.deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 6);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 2, 6);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(minus), 2, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 3, 6);
		IntermediateNode node9 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 3, 5);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 3, 4);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 3, 1);
		node10.addChild(node11);
		TokenSymbolNode node12 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 4, 1);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 5, 6);
		TokenSymbolNode node14 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 5, 1);
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
