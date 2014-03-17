package org.jgll.disambiguation.precedence;

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
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= EPlus E     (non-assoc)
 *     > E + E	  (left)
 *     | a
 * 
 * EPlus ::= EPlus E
 *      | E
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest7 {

	private Grammar grammar;
	private GLLParser parser;
	
	private Nonterminal E = new Nonterminal("E");
	private Nonterminal EPlus = new Nonterminal("EPlus", true);
	private Character a = new Character('a');
	private Character plus = new Character('+');

	@Before
	public void init() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);

		
		// E ::= EPlus E
		Rule rule1 = new Rule(E, list(EPlus, E));
		builder.addRule(rule1);
		
		// E ::=  E + E
		Rule rule2 = new Rule(E, list(E, plus, E));
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = new Rule(E, list(a));
		builder.addRule(rule3);
		
		// EPlus ::= EPlus E
		Rule rule4 = new Rule(EPlus, list(EPlus, E));
		builder.addRule(rule4);
		
		// EPlus ::= E
		Rule rule5 = new Rule(EPlus, list(E));
		builder.addRule(rule5);
		
		// (E ::= .EPlus E, EPlus E) 
		builder.addPrecedencePattern(E, rule1, 0, rule1);
		
		// (E ::= EPlus .E, EPlus E)
		builder.addPrecedencePattern(E, rule1, 1, rule1);
		
		// (E ::= .EPlus E, E + E) 
		builder.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E ::= EPlus .E, E + E)
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
	public void test() throws ParseError {
		Input input = Input.fromString("aaa+aaaa+aaaa");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	private NonterminalSymbolNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 13);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 9);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 8);
		IntermediateNode node4 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 0, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 0, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 0, 1);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 1, 2);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 2, 3);
		TokenSymbolNode node13 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 2, 1);
		node12.addChild(node13);
		node5.addChild(node6);
		node5.addChild(node12);
		TokenSymbolNode node14 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 3, 1);
		node4.addChild(node5);
		node4.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 4, 8);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 4, 7);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 4, 6);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 4, 5);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 4, 5);
		TokenSymbolNode node20 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 4, 1);
		node19.addChild(node20);
		node18.addChild(node19);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 5, 6);
		TokenSymbolNode node22 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 5, 1);
		node21.addChild(node22);
		node17.addChild(node18);
		node17.addChild(node21);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 6, 7);
		TokenSymbolNode node24 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 6, 1);
		node23.addChild(node24);
		node16.addChild(node17);
		node16.addChild(node23);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 7, 8);
		TokenSymbolNode node26 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 7, 1);
		node25.addChild(node26);
		node15.addChild(node16);
		node15.addChild(node25);
		node3.addChild(node4);
		node3.addChild(node15);
		TokenSymbolNode node27 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 8, 1);
		node2.addChild(node3);
		node2.addChild(node27);
		NonterminalSymbolNode node28 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 9, 13);
		NonterminalSymbolNode node29 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 9, 12);
		NonterminalSymbolNode node30 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 9, 11);
		NonterminalSymbolNode node31 = new NonterminalSymbolNode(grammar.getNonterminalId(EPlus), 2, 9, 10);
		NonterminalSymbolNode node32 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 9, 10);
		TokenSymbolNode node33 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 9, 1);
		node32.addChild(node33);
		node31.addChild(node32);
		NonterminalSymbolNode node34 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 10, 11);
		TokenSymbolNode node35 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 10, 1);
		node34.addChild(node35);
		node30.addChild(node31);
		node30.addChild(node34);
		NonterminalSymbolNode node36 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 11, 12);
		TokenSymbolNode node37 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 11, 1);
		node36.addChild(node37);
		node29.addChild(node30);
		node29.addChild(node36);
		NonterminalSymbolNode node38 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 3, 12, 13);
		TokenSymbolNode node39 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 12, 1);
		node38.addChild(node39);
		node28.addChild(node29);
		node28.addChild(node38);
		node1.addChild(node2);
		node1.addChild(node28);
		return node1;
	}

}
