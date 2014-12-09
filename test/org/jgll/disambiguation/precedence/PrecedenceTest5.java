package org.jgll.disambiguation.precedence;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E z   1
 *     > x E   2
 *     > E w   3
 *     > y E   4
 *     | a
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedenceTest5 {

	private GLLParser parser;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Character a = Character.from('a');
	private Character w = Character.from('w');
	private Character x = Character.from('x');
	private Character y = Character.from('y');
	private Character z = Character.from('z');

	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		// E ::= E z
		Rule rule1 = new Rule(E, list(E, z));
		builder.addRule(rule1);
		
		// E ::=  x E
		Rule rule2 = new Rule(E, list(x, E));
		builder.addRule(rule2);
		
		// E ::= E w
		Rule rule3 = new Rule(E, list(E, w));
		builder.addRule(rule3);
		
		// E ::= y E
		Rule rule4 = new Rule(E, list(y, E));
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = new Rule(E, list(a));
		builder.addRule(rule5);
		
		OperatorPrecedence operatorPrecedence = new OperatorPrecedence();
		
		// (E, .E z, x E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule2);
		
		// (E, .E z, y E) 
		operatorPrecedence.addPrecedencePattern(E, rule1, 0, rule4);
		
		// (E, x .E, E w)
		operatorPrecedence.addPrecedencePattern(E, rule2, 1, rule3);
		
		// (E, .E w, y E)
		operatorPrecedence.addPrecedencePattern(E, rule3, 0, rule4);
		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void testParsers() {
		Input input = Input.fromString("xawz");
		parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 4).init();
		PackedNode node2 = factory.createPackedNode("E ::= E1 z .", 3, node1);
		NonterminalNode node3 = factory.createNonterminalNode("E", 1, 0, 3).init();
		PackedNode node4 = factory.createPackedNode("E1 ::= E3 w .", 2, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 3, 0, 2).init();
		PackedNode node6 = factory.createPackedNode("E3 ::= x E5 .", 1, node5);
		TerminalSymbolNode node7 = factory.createTokenNode("x", 0, 1);
		NonterminalNode node8 = factory.createNonterminalNode("E", 5, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("E5 ::= a .", 1, node8);
		TerminalSymbolNode node10 = factory.createTokenNode("a", 1, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		TerminalSymbolNode node11 = factory.createTokenNode("w", 2, 1);
		node4.addChild(node5);
		node4.addChild(node11);
		node3.addChild(node4);
		TerminalSymbolNode node12 = factory.createTokenNode("z", 3, 1);
		node2.addChild(node3);
		node2.addChild(node12);
		node1.addChild(node2);
		return node1;
	}

}
