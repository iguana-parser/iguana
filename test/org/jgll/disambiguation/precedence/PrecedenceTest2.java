package org.jgll.disambiguation.precedence;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E ^ E	(right)
 *     > E + E	(left)
 *     > - E
 *     | a
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest2 {

	private GLLParser parser;

	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character hat = Character.from('^');
	private Character plus = Character.from('+');
	private Character minus = Character.from('-');

	private Grammar grammar;

	
	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E ^ E
		Rule rule0 = Rule.builder(E).addSymbols(E, hat, E).build();
		builder.addRule(rule0);
		
		// E ::= E + E
		Rule rule1 = Rule.builder(E).addSymbols(E, plus, E).build();
		builder.addRule(rule1);
		
		// E ::= E - E
		Rule rule2 = Rule.builder(E).addSymbols(minus, E).build();
		builder.addRule(rule2);
		
		// E ::= a
		Rule rule3 = Rule.builder(E).addSymbols(a).build();
		builder.addRule(rule3);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// left associative E + E
		operatorPrecedence.addPrecedencePattern(E, rule1, 2, rule1);
		
		// + has higher priority than -
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// right associative E ^ E
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule0);
		
		// ^ has higher priority than -
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule2);
		
		// ^ has higher priority than +
		operatorPrecedence.addPrecedencePattern(E, rule0, 0, rule1);
		operatorPrecedence.addPrecedencePattern(E, rule0, 2, rule1);
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void test() {
		Input input = Input.fromString("a+a^a^-a+a");
		parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "E");
		assertTrue(result.isParseSuccess());
		assertEquals(0, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	private SPPFNode getSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 10).init();
		PackedNode node2 = factory.createPackedNode("E ::= E2 + E1 .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("E ::= E2 + . E1", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("E ::= E2 + . E1", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 2, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("E2 ::= a .", 0, node5);
		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		TerminalNode node8 = factory.createTerminalNode("+", 1, 2);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node9 = factory.createNonterminalNode("E", 1, 2, 10).init();
		PackedNode node10 = factory.createPackedNode("E1 ::= E3 ^ E1 .", 4, node9);
		IntermediateNode node11 = factory.createIntermediateNode("E1 ::= E3 ^ . E1", 2, 4).init();
		PackedNode node12 = factory.createPackedNode("E1 ::= E3 ^ . E1", 3, node11);
		NonterminalNode node13 = factory.createNonterminalNode("E", 3, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("E3 ::= a .", 2, node13);
		TerminalNode node15 = factory.createTerminalNode("a", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node16 = factory.createTerminalNode("^", 3, 4);
		node12.addChild(node13);
		node12.addChild(node16);
		node11.addChild(node12);
		NonterminalNode node17 = factory.createNonterminalNode("E", 1, 4, 10).init();
		PackedNode node18 = factory.createPackedNode("E1 ::= E3 ^ E1 .", 6, node17);
		IntermediateNode node19 = factory.createIntermediateNode("E1 ::= E3 ^ . E1", 4, 6).init();
		PackedNode node20 = factory.createPackedNode("E1 ::= E3 ^ . E1", 5, node19);
		NonterminalNode node21 = factory.createNonterminalNode("E", 3, 4, 5).init();
		PackedNode node22 = factory.createPackedNode("E3 ::= a .", 4, node21);
		TerminalNode node23 = factory.createTerminalNode("a", 4, 5);
		node22.addChild(node23);
		node21.addChild(node22);
		TerminalNode node24 = factory.createTerminalNode("^", 5, 6);
		node20.addChild(node21);
		node20.addChild(node24);
		node19.addChild(node20);
		NonterminalNode node25 = factory.createNonterminalNode("E", 1, 6, 10).init();
		PackedNode node26 = factory.createPackedNode("E1 ::= - E .", 7, node25);
		TerminalNode node27 = factory.createTerminalNode("-", 6, 7);
		NonterminalNode node28 = factory.createNonterminalNode("E", 0, 7, 10).init();
		PackedNode node29 = factory.createPackedNode("E ::= E2 + E1 .", 9, node28);
		IntermediateNode node30 = factory.createIntermediateNode("E ::= E2 + . E1", 7, 9).init();
		PackedNode node31 = factory.createPackedNode("E ::= E2 + . E1", 8, node30);
		NonterminalNode node32 = factory.createNonterminalNode("E", 2, 7, 8).init();
		PackedNode node33 = factory.createPackedNode("E2 ::= a .", 7, node32);
		TerminalNode node34 = factory.createTerminalNode("a", 7, 8);
		node33.addChild(node34);
		node32.addChild(node33);
		TerminalNode node35 = factory.createTerminalNode("+", 8, 9);
		node31.addChild(node32);
		node31.addChild(node35);
		node30.addChild(node31);
		NonterminalNode node36 = factory.createNonterminalNode("E", 1, 9, 10).init();
		PackedNode node37 = factory.createPackedNode("E1 ::= a .", 9, node36);
		TerminalNode node38 = factory.createTerminalNode("a", 9, 10);
		node37.addChild(node38);
		node36.addChild(node37);
		node29.addChild(node30);
		node29.addChild(node36);
		node28.addChild(node29);
		node26.addChild(node27);
		node26.addChild(node28);
		node25.addChild(node26);
		node18.addChild(node19);
		node18.addChild(node25);
		node17.addChild(node18);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}

}
