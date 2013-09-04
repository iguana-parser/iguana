package org.jgll.grammar;

import static org.jgll.util.collections.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

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
 * E ::= E * E			  (left)
 * 	   > (E + E | E - E)  (left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest6 {

	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering");
		
		// E ::= E * E
		Nonterminal E = new Nonterminal("E");
		Rule rule1 = new Rule(E, list(E, new Character('*'), E));
		builder.addRule(rule1);		
		
		// E ::= E + E
		Rule rule2 = new Rule(E, list(E, new Character('+'), E));
		builder.addRule(rule2);

		// E ::= E - E
		Rule rule3 = new Rule(E, list(E, new Character('-'), E));
		builder.addRule(rule3);
		
		// E ::= - E
		Rule rule4 = new Rule(E, list(new Character('-'), E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(new Character('a')));
		builder.addRule(rule5);
		
		
		// Patterns:
		
		// E * E (left): 		E * . E, E * E
		builder.addPrecedencePattern(E, rule1, 2, rule1);
		
		// E * E > E + E:		E * . E, E + E and . E * E, E + E
		builder.addPrecedencePattern(E, rule1, 2, rule2);
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// E * E > E - E:		E * . E, E - E
		builder.addPrecedencePattern(E, rule1, 2, rule3);
		
		// E * E > - E:			. E * E, - E
		builder.addPrecedencePattern(E, rule1, 0, rule4);
		
		// E * E > E - E		. E * E, E - E
		builder.addPrecedencePattern(E, rule1, 0, rule3);
		builder.addPrecedencePattern(E, rule1, 2, rule3);
		
		// E + E (left):		E + . E, E + E
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
		// E - E (left):		E - . E, E - E
		builder.addPrecedencePattern(E, rule3, 2, rule3);
		
		// E + E left E - E		E + . E, E - E
		builder.addPrecedencePattern(E, rule2, 2, rule3);
		
		// E - E left E + E		E - . E, E + E 
		builder.addPrecedencePattern(E, rule3, 2, rule2);
		
		// E + E > - E			. E + E, - E
		builder.addPrecedencePattern(E, rule2, 0, rule4);
		
		// E - E > - E			. E - E, - E
		builder.addPrecedencePattern(E, rule3, 0, rule4);
		
		builder.rewritePrecedenceRules();

		grammar = builder.build();
		
		rdParser = ParserFactory.recursiveDescentParser(grammar);
		levelParser = ParserFactory.levelParser(grammar);
	}

	@Test
	public void testParsersy() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("a+-a+a-a-a+a"), grammar, "E");
		NonterminalSymbolNode sppf2 = levelParser.parse(Input.fromString("a+-a+a-a-a+a"), grammar, "E");
		assertTrue(sppf1.deepEquals(sppf2));
	}
	
	@Test
	public void testInput() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a+a--a+-a+a-a-a+a"), grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}

	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 17);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [-] . E3"), 0, 4);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 0, 3);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [+] . E2"), 0, 2);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 0, 1);
		TerminalSymbolNode node6 = new TerminalSymbolNode(97, 0);
		node5.addChild(node6);
		TerminalSymbolNode node7 = new TerminalSymbolNode(43, 1);
		node4.addChild(node5);
		node4.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(97, 2);
		node8.addChild(node9);
		node3.addChild(node4);
		node3.addChild(node8);
		TerminalSymbolNode node10 = new TerminalSymbolNode(45, 3);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 4, 17);
		TerminalSymbolNode node12 = new TerminalSymbolNode(45, 4);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 5, 17);
		IntermediateNode node14 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [+] . E3"), 5, 7);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 5, 6);
		TerminalSymbolNode node16 = new TerminalSymbolNode(97, 5);
		node15.addChild(node16);
		TerminalSymbolNode node17 = new TerminalSymbolNode(43, 6);
		node14.addChild(node15);
		node14.addChild(node17);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 7, 17);
		TerminalSymbolNode node19 = new TerminalSymbolNode(45, 7);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 8, 17);
		IntermediateNode node21 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E4 [+] . E3"), 8, 16);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 15);
		IntermediateNode node23 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [-] . E2"), 8, 14);
		NonterminalSymbolNode node24 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 13);
		IntermediateNode node25 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [-] . E2"), 8, 12);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 11);
		IntermediateNode node27 = new IntermediateNode(grammar.getGrammarSlotByName("E4 ::= E4 [+] . E2"), 8, 10);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 4), 8, 9);
		TerminalSymbolNode node29 = new TerminalSymbolNode(97, 8);
		node28.addChild(node29);
		TerminalSymbolNode node30 = new TerminalSymbolNode(43, 9);
		node27.addChild(node28);
		node27.addChild(node30);
		NonterminalSymbolNode node31 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 10, 11);
		TerminalSymbolNode node32 = new TerminalSymbolNode(97, 10);
		node31.addChild(node32);
		node26.addChild(node27);
		node26.addChild(node31);
		TerminalSymbolNode node33 = new TerminalSymbolNode(45, 11);
		node25.addChild(node26);
		node25.addChild(node33);
		NonterminalSymbolNode node34 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 12, 13);
		TerminalSymbolNode node35 = new TerminalSymbolNode(97, 12);
		node34.addChild(node35);
		node24.addChild(node25);
		node24.addChild(node34);
		TerminalSymbolNode node36 = new TerminalSymbolNode(45, 13);
		node23.addChild(node24);
		node23.addChild(node36);
		NonterminalSymbolNode node37 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 14, 15);
		TerminalSymbolNode node38 = new TerminalSymbolNode(97, 14);
		node37.addChild(node38);
		node22.addChild(node23);
		node22.addChild(node37);
		TerminalSymbolNode node39 = new TerminalSymbolNode(43, 15);
		node21.addChild(node22);
		node21.addChild(node39);
		NonterminalSymbolNode node40 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 3), 16, 17);
		TerminalSymbolNode node41 = new TerminalSymbolNode(97, 16);
		node40.addChild(node41);
		node20.addChild(node21);
		node20.addChild(node40);
		node18.addChild(node19);
		node18.addChild(node20);
		node13.addChild(node14);
		node13.addChild(node18);
		node11.addChild(node12);
		node11.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
