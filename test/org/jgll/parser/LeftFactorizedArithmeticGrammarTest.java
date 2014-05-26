package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class LeftFactorizedArithmeticGrammarTest {
	
	private GrammarGraph grammarGraph;

	Nonterminal E = Nonterminal.withName("E");
	Nonterminal T = Nonterminal.withName("T");
	Nonterminal E1 = Nonterminal.withName("E1");
	Nonterminal F = Nonterminal.withName("F");
	Nonterminal T1 = Nonterminal.withName("T1");
	Character plus = Character.from('+');
	Character star = Character.from('*');
	Character a = Character.from('a');
	Character openPar = Character.from('(');
	Character closePar = Character.from(')');

	@Before
	public void createGrammar() {

		Grammar grammar = new Grammar();
		
		Rule r1 = new Rule(E, list(T, E1));
		Rule r2 = new Rule(E1, list(plus, T, E1));
		Rule r3 = new Rule(E1);
		Rule r4 = new Rule(T, list(F, T1));
		Rule r5 = new Rule(T1, list(star, F, T1));
		Rule r6 = new Rule(T1);
		Rule r7 = new Rule(F, list(openPar, E, closePar));
		Rule r8 = new Rule(F, list(a));
		
		grammar.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8);
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(openPar, a), grammarGraph.getFirstSet(E));
		assertEquals(set(plus, Epsilon.getInstance()), grammarGraph.getFirstSet(E1));
		assertEquals(set(star, Epsilon.getInstance()), grammarGraph.getFirstSet(T1));
		assertEquals(set(openPar, a), grammarGraph.getFirstSet(T));
		assertEquals(set(openPar, a), grammarGraph.getFirstSet(F));
	}
	
	public void testFollowSets() {
		assertEquals(set(closePar, EOF.getInstance()), grammarGraph.getFollowSet(E));
		assertEquals(set(closePar, EOF.getInstance()), grammarGraph.getFollowSet(E1));
		assertEquals(set(plus, closePar, EOF.getInstance()), grammarGraph.getFollowSet(T1));
		assertEquals(set(plus, closePar, EOF.getInstance()), grammarGraph.getFollowSet(T));
		assertEquals(set(plus, star, closePar, EOF.getInstance()), grammarGraph.getFollowSet(F));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a+a");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "E");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E), 1, 0, 7);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T), 1, 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(F), 2, 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T1), 2, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E1), 2, 1, 7);
		IntermediateNode node7 = new IntermediateNode(grammarGraph.getIntermediateNodeId(plus, T), 1, 5);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 1, 1);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T), 1, 2, 5);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(F), 2, 2, 3);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 2, 1);
		node10.addChild(node11);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T1), 2, 3, 5);
		IntermediateNode node13 = new IntermediateNode(grammarGraph.getIntermediateNodeId(star, F), 3, 5);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(star), 3, 1);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(F), 2, 4, 5);
		TokenSymbolNode node16 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 4, 1);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T1), 2, 5, 5);
		node12.addChild(node13);
		node12.addChild(node17);
		node9.addChild(node10);
		node9.addChild(node12);
		node7.addChild(node8);
		node7.addChild(node9);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E1), 2, 5, 7);
		IntermediateNode node19 = new IntermediateNode(grammarGraph.getIntermediateNodeId(plus, T), 5, 7);
		TokenSymbolNode node20 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(plus), 5, 1);
		NonterminalSymbolNode node21 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T), 1, 6, 7);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(F), 2, 6, 7);
		TokenSymbolNode node23 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 6, 1);
		node22.addChild(node23);
		NonterminalSymbolNode node24 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(T1), 2, 7, 7);
		node21.addChild(node22);
		node21.addChild(node24);
		node19.addChild(node20);
		node19.addChild(node21);
		NonterminalSymbolNode node25 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(E1), 2, 7, 7);
		node18.addChild(node19);
		node18.addChild(node25);
		node6.addChild(node7);
		node6.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
