package org.iguana.disambiguation.precedence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
		Rule rule1 = Rule.withHead(E).addSymbols(E, z).build();
		builder.addRule(rule1);
		
		// E ::=  x E
		Rule rule2 = Rule.withHead(E).addSymbols(x, E).build();
		builder.addRule(rule2);
		
		// E ::= E w
		Rule rule3 = Rule.withHead(E).addSymbols(E, w).build();
		builder.addRule(rule3);
		
		// E ::= y E
		Rule rule4 = Rule.withHead(E).addSymbols(y, E).build();
		builder.addRule(rule4);
		
		// E ::= a
		Rule rule5 = Rule.withHead(E).addSymbols(a).build();
		builder.addRule(rule5);
		
		
		List<PrecedencePattern> list = new ArrayList<>();
		// (E, .E z, x E) 
		list.add(PrecedencePattern.from(rule1, 0, rule2));
		
		// (E, .E z, y E) 
		list.add(PrecedencePattern.from(rule1, 0, rule4));
		
		// (E, x .E, E w)
		list.add(PrecedencePattern.from(rule2, 1, rule3));
		
		// (E, .E w, y E)
		list.add(PrecedencePattern.from(rule3, 0, rule4));

		OperatorPrecedence operatorPrecedence = new OperatorPrecedence(list);		
		grammar = operatorPrecedence.transform(builder.build());
	}

	@Test
	public void testParsers() {
		Input input = Input.fromString("xawz");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
	
	private SPPFNode getSPPF(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 0, 4);
		PackedNode node2 = factory.createPackedNode("E ::= E1 z .", 3, node1);
		NonterminalNode node3 = factory.createNonterminalNode("E", 1, 0, 3);
		PackedNode node4 = factory.createPackedNode("E1 ::= E3 w .", 2, node3);
		NonterminalNode node5 = factory.createNonterminalNode("E", 3, 0, 2);
		PackedNode node6 = factory.createPackedNode("E3 ::= x E5 .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("x", 0, 1);
		NonterminalNode node8 = factory.createNonterminalNode("E", 5, 1, 2);
		PackedNode node9 = factory.createPackedNode("E5 ::= a .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("a", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		TerminalNode node11 = factory.createTerminalNode("w", 2, 3);
		node4.addChild(node5);
		node4.addChild(node11);
		node3.addChild(node4);
		TerminalNode node12 = factory.createTerminalNode("z", 3, 4);
		node2.addChild(node3);
		node2.addChild(node12);
		node1.addChild(node2);
		return node1;
	}

}
