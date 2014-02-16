package org.jgll.grammar.precedence;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
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
	private GLLParser parser;
	
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
		
		builder.rewritePatterns();

		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("aaa+aaaa+aaaa");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 13);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 9);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 8);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("E ::= E [+] . E2"), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("E"), 0, 3);
		ListSymbolNode node6 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 2);
		ListSymbolNode node7 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 0, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 0, 1);
		TokenSymbolNode node9 = new TokenSymbolNode(3, 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(3, 1, 1);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(3, 2, 1);
		node12.addChild(node13);
		node5.addChild(node6);
		node5.addChild(node12);
		TokenSymbolNode node14 = new TokenSymbolNode(2, 3, 1);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 4, 8);
		ListSymbolNode node16 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 7);
		ListSymbolNode node17 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 6);
		ListSymbolNode node18 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 4, 5);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 4, 5);
		TokenSymbolNode node20 = new TokenSymbolNode(3, 4, 1);
		node19.addChild(node20);
		node18.addChild(node19);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 5, 6);
		TokenSymbolNode node22 = new TokenSymbolNode(3, 5, 1);
		node21.addChild(node22);
		node17.addChild(node18);
		node17.addChild(node21);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 6, 7);
		TokenSymbolNode node24 = new TokenSymbolNode(3, 6, 1);
		node23.addChild(node24);
		node16.addChild(node17);
		node16.addChild(node23);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 7, 8);
		TokenSymbolNode node26 = new TokenSymbolNode(3, 7, 1);
		node25.addChild(node26);
		node15.addChild(node16);
		node15.addChild(node25);
		node3.addChild(node4);
		node3.addChild(node15);
		TokenSymbolNode node27 = new TokenSymbolNode(2, 8, 1);
		node2.addChild(node3);
		node2.addChild(node27);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 2), 9, 13);
		ListSymbolNode node29 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 12);
		ListSymbolNode node30 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 11);
		ListSymbolNode node31 = new ListSymbolNode(grammar.getNonterminalByNameAndIndex("E+", 1), 9, 10);
		NonterminalSymbolNode node32 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 9, 10);
		TokenSymbolNode node33 = new TokenSymbolNode(3, 9, 1);
		node32.addChild(node33);
		node31.addChild(node32);
		NonterminalSymbolNode node34 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 10, 11);
		TokenSymbolNode node35 = new TokenSymbolNode(3, 10, 1);
		node34.addChild(node35);
		node30.addChild(node31);
		node30.addChild(node34);
		NonterminalSymbolNode node36 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 11, 12);
		TokenSymbolNode node37 = new TokenSymbolNode(3, 11, 1);
		node36.addChild(node37);
		node29.addChild(node30);
		node29.addChild(node36);
		NonterminalSymbolNode node38 = new NonterminalSymbolNode(grammar.getNonterminalByNameAndIndex("E", 1), 12, 13);
		TokenSymbolNode node39 = new TokenSymbolNode(3, 12, 1);
		node38.addChild(node39);
		node28.addChild(node29);
		node28.addChild(node38);
		node1.addChild(node2);
		node1.addChild(node28);
		return node1;
	}

}
