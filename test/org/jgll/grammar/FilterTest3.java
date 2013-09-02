package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E E+    (non-assoc)
 *     > E + E
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest3 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;
	
	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E E+
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Nonterminal("E+", true)));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(new Nonterminal("E+", true), list(new Nonterminal("E+", true), E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(new Nonterminal("E+", true), list(E));
		builder.addRule(rule5);
		
		// (E ::= .E E+, E E+) 
		builder.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		builder.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		builder.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E .E, E + E)
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
		builder.rewritePrecedenceRules();
		
		grammar = builder.build();
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	public void testParsers() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa+aaa+aa"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}

	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 10);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 8);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 7);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 0, 1);
		TerminalSymbolNode node7 = new TerminalSymbolNode(97, 0);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 1, 3);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 1, 2);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 1, 2);
		TerminalSymbolNode node11 = new TerminalSymbolNode(97, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 3);
		TerminalSymbolNode node13 = new TerminalSymbolNode(97, 2);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node5.addChild(node6);
		node5.addChild(node8);
		TerminalSymbolNode node14 = new TerminalSymbolNode(43, 3);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 4, 7);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 5);
		TerminalSymbolNode node17 = new TerminalSymbolNode(97, 4);
		node16.addChild(node17);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 5, 7);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 5, 6);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6);
		TerminalSymbolNode node21 = new TerminalSymbolNode(97, 5);
		node20.addChild(node21);
		node19.addChild(node20);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 7);
		TerminalSymbolNode node23 = new TerminalSymbolNode(97, 6);
		node22.addChild(node23);
		node18.addChild(node19);
		node18.addChild(node22);
		node15.addChild(node16);
		node15.addChild(node18);
		node3.addChild(node4);
		node3.addChild(node15);
		TerminalSymbolNode node24 = new TerminalSymbolNode(43, 7);
		node2.addChild(node3);
		node2.addChild(node24);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 8, 10);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 8, 9);
		TerminalSymbolNode node27 = new TerminalSymbolNode(97, 8);
		node26.addChild(node27);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 10);
		NonterminalSymbolNode node29 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 9, 10);
		TerminalSymbolNode node30 = new TerminalSymbolNode(97, 9);
		node29.addChild(node30);
		node28.addChild(node29);
		node25.addChild(node26);
		node25.addChild(node28);
		node1.addChild(node2);
		node1.addChild(node25);
		return node1;
	}

}
