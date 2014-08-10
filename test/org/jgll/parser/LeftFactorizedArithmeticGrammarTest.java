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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
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

	private Grammar grammar;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal T = Nonterminal.withName("T");
	private Nonterminal E1 = Nonterminal.withName("E1");
	private Nonterminal F = Nonterminal.withName("F");
	private Nonterminal T1 = Nonterminal.withName("T1");
	private Character plus = Character.from('+');
	private Character star = Character.from('*');
	private Character a = Character.from('a');
	private Character openPar = Character.from('(');
	private Character closePar = Character.from(')');

	@Before
	public void createGrammar() {

		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = new Rule(E, list(T, E1));
		Rule r2 = new Rule(E1, list(plus, T, E1));
		Rule r3 = new Rule(E1);
		Rule r4 = new Rule(T, list(F, T1));
		Rule r5 = new Rule(T1, list(star, F, T1));
		Rule r6 = new Rule(T1);
		Rule r7 = new Rule(F, list(openPar, E, closePar));
		Rule r8 = new Rule(F, list(a));
		
		grammar = builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8).build();
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(openPar, a), grammar.getFirstSet(E));
		assertEquals(set(plus, Epsilon.getInstance()), grammar.getFirstSet(E1));
		assertEquals(set(star, Epsilon.getInstance()), grammar.getFirstSet(T1));
		assertEquals(set(openPar, a), grammar.getFirstSet(T));
		assertEquals(set(openPar, a), grammar.getFirstSet(F));
	}
	
	public void testFollowSets() {
		assertEquals(set(closePar, EOF.getInstance()), grammar.getFollowSet(E));
		assertEquals(set(closePar, EOF.getInstance()), grammar.getFollowSet(E1));
		assertEquals(set(plus, closePar, EOF.getInstance()), grammar.getFollowSet(T1));
		assertEquals(set(plus, closePar, EOF.getInstance()), grammar.getFollowSet(T));
		assertEquals(set(plus, star, closePar, EOF.getInstance()), grammar.getFollowSet(F));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a+a*a+a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "E");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(E, 0, 7);
		NonterminalNode node2 = factory.createNonterminalNode(T, 0, 1);
		NonterminalNode node3 = factory.createNonterminalNode(F, 0, 1);
		TokenSymbolNode node4 = factory.createTokenNode(a, 0, 1);
		node3.addChild(node4);
		NonterminalNode node5 = factory.createNonterminalNode(T1, 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(E1, 1, 7);
		IntermediateNode node7 = factory.createIntermediateNode(list(plus, T), 1, 5);
		TokenSymbolNode node8 = factory.createTokenNode(plus, 1, 1);
		NonterminalNode node9 = factory.createNonterminalNode(T, 2, 5);
		NonterminalNode node10 = factory.createNonterminalNode(F, 2, 3);
		TokenSymbolNode node11 = factory.createTokenNode(a, 2, 1);
		node10.addChild(node11);
		NonterminalNode node12 = factory.createNonterminalNode(T1, 3, 5);
		IntermediateNode node13 = factory.createIntermediateNode(list(star, F), 3, 5);
		TokenSymbolNode node14 = factory.createTokenNode(star, 3, 1);
		NonterminalNode node15 = factory.createNonterminalNode(F, 4, 5);
		TokenSymbolNode node16 = factory.createTokenNode(a, 4, 1);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		NonterminalNode node17 = factory.createNonterminalNode(T1, 5, 5);
		node12.addChild(node13);
		node12.addChild(node17);
		node9.addChild(node10);
		node9.addChild(node12);
		node7.addChild(node8);
		node7.addChild(node9);
		NonterminalNode node18 = factory.createNonterminalNode(E1, 5, 7);
		IntermediateNode node19 = factory.createIntermediateNode(list(plus, T), 5, 7);
		TokenSymbolNode node20 = factory.createTokenNode(plus, 5, 1);
		NonterminalNode node21 = factory.createNonterminalNode(T, 6, 7);
		NonterminalNode node22 = factory.createNonterminalNode(F, 6, 7);
		TokenSymbolNode node23 = factory.createTokenNode(a, 6, 1);
		node22.addChild(node23);
		NonterminalNode node24 = factory.createNonterminalNode(T1, 7, 7);
		node21.addChild(node22);
		node21.addChild(node24);
		node19.addChild(node20);
		node19.addChild(node21);
		NonterminalNode node25 = factory.createNonterminalNode(E1, 7, 7);
		node18.addChild(node19);
		node18.addChild(node25);
		node6.addChild(node7);
		node6.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}
