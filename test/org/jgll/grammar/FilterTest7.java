package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E+ E     (non-assoc)
 *     > E + E	  (left)
 *     | a
 * 
 * E+ ::= E+ E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest7 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;
	
	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");

		Nonterminal E = new Nonterminal("E");
		Nonterminal Eplus = new Nonterminal("E+", true);
		
		// E ::= E+ E
		Rule rule1 = new Rule(E, list(Eplus, E));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(new Character('a')));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(Eplus, list(Eplus, E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(Eplus, list(E));
		builder.addRule(rule5);
		
		// (E ::= .E+ E, E+ E) 
		builder.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E+ .E, E+ E)
		builder.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E+ E, E + E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E+ .E, E + E)
		builder.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E + .E, E + E)
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
		builder.addExceptPattern(Eplus, rule4, 1, rule1);
		builder.addExceptPattern(Eplus, rule4, 1, rule2);
		builder.addExceptPattern(Eplus, rule5, 0, rule1);
		builder.addExceptPattern(Eplus, rule5, 0, rule2);
		
		builder.rewriteExceptPatterns();
		builder.rewritePrecedencePatterns();

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
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa+aaaa+aaaa"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}

	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 13);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E3"), 0, 9);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 8);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E3"), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 3);
		ListSymbolNode node6 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 2);
		ListSymbolNode node7 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 0, 1);
		TerminalSymbolNode node9 = new TerminalSymbolNode(97, 0);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 1, 2);
		TerminalSymbolNode node11 = new TerminalSymbolNode(97, 1);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 2, 3);
		TerminalSymbolNode node13 = new TerminalSymbolNode(97, 2);
		node12.addChild(node13);
		node5.addChild(node6);
		node5.addChild(node12);
		TerminalSymbolNode node14 = new TerminalSymbolNode(43, 3);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 4, 8);
		ListSymbolNode node16 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 7);
		ListSymbolNode node17 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 6);
		ListSymbolNode node18 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 5);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 4, 5);
		TerminalSymbolNode node20 = new TerminalSymbolNode(97, 4);
		node19.addChild(node20);
		node18.addChild(node19);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6);
		TerminalSymbolNode node22 = new TerminalSymbolNode(97, 5);
		node21.addChild(node22);
		node17.addChild(node18);
		node17.addChild(node21);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 7);
		TerminalSymbolNode node24 = new TerminalSymbolNode(97, 6);
		node23.addChild(node24);
		node16.addChild(node17);
		node16.addChild(node23);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 7, 8);
		TerminalSymbolNode node26 = new TerminalSymbolNode(97, 7);
		node25.addChild(node26);
		node15.addChild(node16);
		node15.addChild(node25);
		node3.addChild(node4);
		node3.addChild(node15);
		TerminalSymbolNode node27 = new TerminalSymbolNode(43, 8);
		node2.addChild(node3);
		node2.addChild(node27);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 9, 13);
		ListSymbolNode node29 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 12);
		ListSymbolNode node30 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 11);
		ListSymbolNode node31 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 10);
		NonterminalSymbolNode node32 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 9, 10);
		TerminalSymbolNode node33 = new TerminalSymbolNode(97, 9);
		node32.addChild(node33);
		node31.addChild(node32);
		NonterminalSymbolNode node34 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 10, 11);
		TerminalSymbolNode node35 = new TerminalSymbolNode(97, 10);
		node34.addChild(node35);
		node30.addChild(node31);
		node30.addChild(node34);
		NonterminalSymbolNode node36 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 11, 12);
		TerminalSymbolNode node37 = new TerminalSymbolNode(97, 11);
		node36.addChild(node37);
		node29.addChild(node30);
		node29.addChild(node36);
		NonterminalSymbolNode node38 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 12, 13);
		TerminalSymbolNode node39 = new TerminalSymbolNode(97, 12);
		node38.addChild(node39);
		node28.addChild(node29);
		node28.addChild(node38);
		node1.addChild(node2);
		node1.addChild(node28);
		return node1;
	}

}
