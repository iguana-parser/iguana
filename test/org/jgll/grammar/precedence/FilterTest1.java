package org.jgll.grammar.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
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
import org.jgll.util.Visualization;
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

	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);
		
		// E ::= E + E
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule1);
		
		// E ::= - E
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		grammar = builder.build();
		System.out.println(grammar);
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("a+-a+a");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, grammar, input);
		assertTrue(sppf.deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 0, 6);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(4, 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(2, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 2, 6);
		TokenSymbolNode node7 = new TokenSymbolNode(3, 2, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 3, 6);
		IntermediateNode node9 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 3, 5);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 3, 4);
		TokenSymbolNode node11 = new TokenSymbolNode(4, 3, 1);
		node10.addChild(node11);
		TokenSymbolNode node12 = new TokenSymbolNode(2, 4, 1);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("E"), 5, 6);
		TokenSymbolNode node14 = new TokenSymbolNode(4, 5, 1);
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
