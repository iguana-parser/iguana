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
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
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

	private Grammar grammar;
	private GLLParser parser;
	
	private Nonterminal E = new Nonterminal("E");
	private Nonterminal EPlus = new Nonterminal("E+", true);
	private Character a = new Character('a');
	private Character plus = new Character('+');
	
	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);
		
		
		// E ::= E E+
		Rule rule1 = new Rule(E, list(E, EPlus));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		// E+ ::= E+ E
		Rule rule4 = new Rule(EPlus, list(EPlus, E));
		builder.addRule(rule4);
		
		// E+ ::= E
		Rule rule5 = new Rule(EPlus, list(E));
		builder.addRule(rule5);
		
		// (E ::= .E E+, E E+)
		builder.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= E .E+, E E+)
		builder.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .E E+, E + E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= E .E+, E + E)
		builder.addPrecedencePattern(E, rule1, 1, rule2);
		
		// (E ::= E + .E, E + E)
		builder.addPrecedencePattern(E, rule2, 2, rule2);
		
		builder.addExceptPattern(EPlus, rule4, 1, rule1);
		builder.addExceptPattern(EPlus, rule4, 1, rule2);
		builder.addExceptPattern(EPlus, rule5, 0, rule1);
		builder.addExceptPattern(EPlus, rule5, 0, rule2);
		
		grammar = builder.build();
	}

	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("aaa+aaaaa+aaaa");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, grammar, input);
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 14);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 10);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 9);
		IntermediateNode node4 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 1);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 1, 3);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 1, 2);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 2, 1);
		node12.addChild(node13);
		node8.addChild(node9);
		node8.addChild(node12);
		node5.addChild(node6);
		node5.addChild(node8);
		TokenSymbolNode node14 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 3, 1);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 4, 9);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 4, 5);
		TokenSymbolNode node17 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 4, 1);
		node16.addChild(node17);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 5, 9);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 5, 8);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 5, 7);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 5, 6);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 5, 6);
		TokenSymbolNode node23 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 5, 1);
		node22.addChild(node23);
		node21.addChild(node22);
		NonterminalSymbolNode node24 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 6, 7);
		TokenSymbolNode node25 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 6, 1);
		node24.addChild(node25);
		node20.addChild(node21);
		node20.addChild(node24);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 7, 8);
		TokenSymbolNode node27 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 7, 1);
		node26.addChild(node27);
		node19.addChild(node20);
		node19.addChild(node26);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 8, 9);
		TokenSymbolNode node29 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 8, 1);
		node28.addChild(node29);
		node18.addChild(node19);
		node18.addChild(node28);
		node15.addChild(node16);
		node15.addChild(node18);
		node3.addChild(node4);
		node3.addChild(node15);
		TokenSymbolNode node30 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 9, 1);
		node2.addChild(node3);
		node2.addChild(node30);
		NonterminalSymbolNode node31 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 10, 14);
		NonterminalSymbolNode node32 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 10, 11);
		TokenSymbolNode node33 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 10, 1);
		node32.addChild(node33);
		NonterminalSymbolNode node34 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 11, 14);
		NonterminalSymbolNode node35 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 11, 13);
		NonterminalSymbolNode node36 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 11, 12);
		NonterminalSymbolNode node37 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 11, 12);
		TokenSymbolNode node38 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 11, 1);
		node37.addChild(node38);
		node36.addChild(node37);
		NonterminalSymbolNode node39 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 12, 13);
		TokenSymbolNode node40 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 12, 1);
		node39.addChild(node40);
		node35.addChild(node36);
		node35.addChild(node39);
		NonterminalSymbolNode node41 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 13, 14);
		TokenSymbolNode node42 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 13, 1);
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
