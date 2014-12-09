package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalSymbolNode;
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
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("E", 0, 7).init();
		PackedNode node2 = factory.createPackedNode("E ::= T E1 .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("T", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("T ::= F T1 .", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("F", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("F ::= a .", 0, node5);
		TerminalSymbolNode node7 = factory.createTokenNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("T1", 1, 1).init();
		PackedNode node9 = factory.createPackedNode("T1 ::= .", 1, node8);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node10 = factory.createNonterminalNode("E1", 1, 7).init();
		PackedNode node11 = factory.createPackedNode("E1 ::= + T E1 .", 5, node10);
		IntermediateNode node12 = factory.createIntermediateNode("E1 ::= + T . E1", 1, 5).init();
		PackedNode node13 = factory.createPackedNode("E1 ::= + T . E1", 2, node12);
		TerminalSymbolNode node14 = factory.createTokenNode("+", 1, 1);
		NonterminalNode node15 = factory.createNonterminalNode("T", 2, 5).init();
		PackedNode node16 = factory.createPackedNode("T ::= F T1 .", 3, node15);
		NonterminalNode node17 = factory.createNonterminalNode("F", 2, 3).init();
		PackedNode node18 = factory.createPackedNode("F ::= a .", 2, node17);
		TerminalSymbolNode node19 = factory.createTokenNode("a", 2, 1);
		node18.addChild(node19);
		node17.addChild(node18);
		NonterminalNode node20 = factory.createNonterminalNode("T1", 3, 5).init();
		PackedNode node21 = factory.createPackedNode("T1 ::= * F T1 .", 5, node20);
		IntermediateNode node22 = factory.createIntermediateNode("T1 ::= * F . T1", 3, 5).init();
		PackedNode node23 = factory.createPackedNode("T1 ::= * F . T1", 4, node22);
		TerminalSymbolNode node24 = factory.createTokenNode("*", 3, 1);
		NonterminalNode node25 = factory.createNonterminalNode("F", 4, 5).init();
		PackedNode node26 = factory.createPackedNode("F ::= a .", 4, node25);
		TerminalSymbolNode node27 = factory.createTokenNode("a", 4, 1);
		node26.addChild(node27);
		node25.addChild(node26);
		node23.addChild(node24);
		node23.addChild(node25);
		node22.addChild(node23);
		NonterminalNode node28 = factory.createNonterminalNode("T1", 5, 5).init();
		PackedNode node29 = factory.createPackedNode("T1 ::= .", 5, node28);
		node28.addChild(node29);
		node21.addChild(node22);
		node21.addChild(node28);
		node20.addChild(node21);
		node16.addChild(node17);
		node16.addChild(node20);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		node12.addChild(node13);
		NonterminalNode node30 = factory.createNonterminalNode("E1", 5, 7).init();
		PackedNode node31 = factory.createPackedNode("E1 ::= + T E1 .", 7, node30);
		IntermediateNode node32 = factory.createIntermediateNode("E1 ::= + T . E1", 5, 7).init();
		PackedNode node33 = factory.createPackedNode("E1 ::= + T . E1", 6, node32);
		TerminalSymbolNode node34 = factory.createTokenNode("+", 5, 1);
		NonterminalNode node35 = factory.createNonterminalNode("T", 6, 7).init();
		PackedNode node36 = factory.createPackedNode("T ::= F T1 .", 7, node35);
		NonterminalNode node37 = factory.createNonterminalNode("F", 6, 7).init();
		PackedNode node38 = factory.createPackedNode("F ::= a .", 6, node37);
		TerminalSymbolNode node39 = factory.createTokenNode("a", 6, 1);
		node38.addChild(node39);
		node37.addChild(node38);
		NonterminalNode node40 = factory.createNonterminalNode("T1", 7, 7).init();
		PackedNode node41 = factory.createPackedNode("T1 ::= .", 7, node40);
		node40.addChild(node41);
		node36.addChild(node37);
		node36.addChild(node40);
		node35.addChild(node36);
		node33.addChild(node34);
		node33.addChild(node35);
		node32.addChild(node33);
		NonterminalNode node42 = factory.createNonterminalNode("E1", 7, 7).init();
		PackedNode node43 = factory.createPackedNode("E1 ::= .", 7, node42);
		node42.addChild(node43);
		node31.addChild(node32);
		node31.addChild(node42);
		node30.addChild(node31);
		node11.addChild(node12);
		node11.addChild(node30);
		node10.addChild(node11);
		node2.addChild(node3);
		node2.addChild(node10);
		node1.addChild(node2);
		return node1;
	}

}
