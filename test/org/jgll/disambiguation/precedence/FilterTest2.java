package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

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

	private Grammar grammar;
	private GLLParser parser;

	private Nonterminal E = new Nonterminal("E");
	private Character a = new Character('a');
	private Character hat = new Character('^');
	private Character plus = new Character('+');
	private Character minus = new Character('-');

	
	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);
		
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
		
		// left associative E + E
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		
		// + has higher priority than -
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// right associative E ^ E
		builder.addPrecedencePattern(E, rule0, 0, rule0);
		
		// ^ has higher priority than -
		builder.addPrecedencePattern(E, rule0, 0, rule2);
		
		// ^ has higher priority than +
		builder.addPrecedencePattern(E, rule0, 0, rule1);
		builder.addPrecedencePattern(E, rule0, 2, rule1);
		
		grammar = builder.build();
	}

	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("a+a^a^-a+a");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 0, 10);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 2, 10);
		IntermediateNode node7 = new IntermediateNode(grammar.getIntermediateNodeId(E, hat), 2, 4);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 2, 1);
		node8.addChild(node9);
		TokenSymbolNode node10 = new TokenSymbolNode(grammar.getRegularExpressionId(hat), 3, 1);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 4, 10);
		IntermediateNode node12 = new IntermediateNode(grammar.getIntermediateNodeId(E, hat), 4, 6);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 4, 5);
		TokenSymbolNode node14 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 4, 1);
		node13.addChild(node14);
		TokenSymbolNode node15 = new TokenSymbolNode(grammar.getRegularExpressionId(hat), 5, 1);
		node12.addChild(node13);
		node12.addChild(node15);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 6, 10);
		TokenSymbolNode node17 = new TokenSymbolNode(grammar.getRegularExpressionId(minus), 6, 1);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 7, 10);
		IntermediateNode node19 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 7, 9);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 7, 8);
		TokenSymbolNode node21 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 7, 1);
		node20.addChild(node21);
		TokenSymbolNode node22 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 8, 1);
		node19.addChild(node20);
		node19.addChild(node22);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 4, 9, 10);
		TokenSymbolNode node24 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 9, 1);
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
