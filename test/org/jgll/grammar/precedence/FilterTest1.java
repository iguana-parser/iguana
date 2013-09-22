package org.jgll.grammar.precedence;

import static org.junit.Assert.*;
import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Rule;
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
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
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
		
		builder.rewritePrecedencePatterns();
		
		grammar = builder.build();
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	@Test
	public void testAssociativityAndPriority() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+-a+a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a+-a+a"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPFNode()));
	}
	
	private SPPFNode getSPPFNode() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 6);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 0, 1);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 0);
		node3.addChild(node4);
		TerminalSymbolNode node5 = new TerminalSymbolNode(43, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 6);
		TerminalSymbolNode node7 = new TerminalSymbolNode(45, 2);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 3, 6);
		IntermediateNode node9 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E2 [+] . E1"), 3, 5);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 3, 4);
		TerminalSymbolNode node11 = new TerminalSymbolNode(97, 3);
		node10.addChild(node11);
		TerminalSymbolNode node12 = new TerminalSymbolNode(43, 4);
		node9.addChild(node10);
		node9.addChild(node12);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6);
		TerminalSymbolNode node14 = new TerminalSymbolNode(97, 5);
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
