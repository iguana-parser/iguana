package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * E ::= E [ E ]
 * 	   | E +
 * 	   | E *
 * 	   > E + E
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class FilterTest8 {
	
	private Nonterminal E = new Nonterminal("E");
	private Character a = new Character('a');
	private Character plus = new Character('+');
	private Character star = new Character('*');
	private Character ob = new Character('[');
	private Character cb = new Character(']');
	
	private Grammar grammar;
	private GLLParser parser;
	
	@Before
	public void init() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("Test8", factory);

		// E ::= E [ E ]
		Rule rule1 = new Rule(E, list(E, ob, E, cb));
		builder.addRule(rule1);
		
		// E ::= E +
		Rule rule2 = new Rule(E, list(E, plus));
		builder.addRule(rule2);
		
		// E ::= E *
		Rule rule3 = new Rule(E, list(E, star));
		builder.addRule(rule3);
		
		// E ::= E + E
		Rule rule4 = new Rule(E, list(E, plus, E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(a));
		builder.addRule(rule5);
		
		// (E, .E [ E ], E + E)
		builder.addPrecedencePattern(E, rule1, 0, rule4);
		
		// (E, .E *, E + E)
		builder.addPrecedencePattern(E, rule2, 0, rule4);
		
		// (E, .E +, E + E)
		builder.addPrecedencePattern(E, rule3, 0, rule4);

		builder.addExceptPattern(E, rule1, 0, rule1);
		builder.addExceptPattern(E, rule1, 0, rule2);
		builder.addExceptPattern(E, rule1, 0, rule3);
	
		grammar = builder.build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("a+a[a+a]");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void test2() throws ParseError {
		Input input = Input.fromString("a+a*a+[a+a]");
		parser = ParserFactory.newParser(grammar, input);
		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "E");
	}
	
	@Test
	public void test3() throws ParseError {
		Input input = Input.fromString("a[a][a+a]");
		parser = ParserFactory.newParser(grammar, input);
		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "E");
	}	


	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 0, 8);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 2, 8);
		IntermediateNode node7 = new IntermediateNode(grammar.getIntermediateNodeId(E, ob, E), 2, 7);
		IntermediateNode node8 = new IntermediateNode(grammar.getIntermediateNodeId(E, ob), 2, 4);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 2, 3);
		TokenSymbolNode node10 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 2, 1);
		node9.addChild(node10);
		TokenSymbolNode node11 = new TokenSymbolNode(grammar.getRegularExpressionId(ob), 3, 1);
		node8.addChild(node9);
		node8.addChild(node11);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 4, 7);
		IntermediateNode node13 = new IntermediateNode(grammar.getIntermediateNodeId(E, plus), 4, 6);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 4, 5);
		TokenSymbolNode node15 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 4, 1);
		node14.addChild(node15);
		TokenSymbolNode node16 = new TokenSymbolNode(grammar.getRegularExpressionId(plus), 5, 1);
		node13.addChild(node14);
		node13.addChild(node16);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalId(E), 5, 6, 7);
		TokenSymbolNode node18 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 6, 1);
		node17.addChild(node18);
		node12.addChild(node13);
		node12.addChild(node17);
		node7.addChild(node8);
		node7.addChild(node12);
		TokenSymbolNode node19 = new TokenSymbolNode(grammar.getRegularExpressionId(cb), 7, 1);
		node6.addChild(node7);
		node6.addChild(node19);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
