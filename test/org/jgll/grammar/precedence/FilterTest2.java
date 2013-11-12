package org.jgll.grammar.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= 0 E ^ E	(right)
 *     > 1 E + E	(left)
 *     > 2 - E
 *     | 3 a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest2 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	
	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		Nonterminal E = new Nonterminal("E");
		Rule rule0 = new Rule(E, list(E, new Character('^'), E));
		builder.addRule(rule0);
		
		Rule rule1 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(E, list(new Character('a')));
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
		
		builder.rewritePrecedencePatterns();
		
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
		levelParser = ParserFactory.createLevelParser(grammar);
	}
	

	@Test
	public void testAssociativityAndPriority() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+a^a^-a+a"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 10);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 0, 1);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 0);
		node3.addChild(node4);
		TerminalSymbolNode node5 = new TerminalSymbolNode(43, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 10);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("E1 ::= E3 [^] . E1"), 2, 4);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(97, 2);
		node8.addChild(node9);
		TerminalSymbolNode node10 = new TerminalSymbolNode(94, 3);
		node7.addChild(node8);
		node7.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 10);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("E1 ::= E3 [^] . E1"), 4, 6);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 4, 5);
		TerminalSymbolNode node14 = new TerminalSymbolNode(97, 4);
		node13.addChild(node14);
		TerminalSymbolNode node15 = new TerminalSymbolNode(94, 5);
		node12.addChild(node13);
		node12.addChild(node15);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 10);
		TerminalSymbolNode node17 = new TerminalSymbolNode(45, 6);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 7, 10);
		IntermediateNode node19 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 7, 9);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 7, 8);
		TerminalSymbolNode node21 = new TerminalSymbolNode(97, 7);
		node20.addChild(node21);
		TerminalSymbolNode node22 = new TerminalSymbolNode(43, 8);
		node19.addChild(node20);
		node19.addChild(node22);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 9, 10);
		TerminalSymbolNode node24 = new TerminalSymbolNode(97, 9);
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
