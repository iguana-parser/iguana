package org.jgll.grammar;


import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.ToJavaCode;
import org.junit.Test;

public class Gamma0Test extends AbstractGrammarTest {

	/**
	 *	S ::= a S | A S d | epsilon
	 * 	A ::= a
	 */
	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("S")).body(new Nonterminal("A"), new Nonterminal("S"), new Character('d')).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("S")).build();
		Rule r4 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("gamma1", asList(r1, r2, r3, r4));
	}
	
	@Test
	public void testNullables() {
		assertEquals(true, grammar.getNonterminalByName("S").isNullable());
		assertEquals(false, grammar.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void testLongestGrammarChain() {
		assertEquals(1, grammar.getLongestTerminalChain());
	}
	
	@Test
	public void testFirstSets() {
		assertEquals(set(new Character('a'), Epsilon.getInstance()), grammar.getNonterminalByName("S").getFirstSet());
		assertEquals(set(new Character('a')), grammar.getNonterminalByName("A").getFirstSet());
	}

	@Test
	public void testFollowSets() {
		assertEquals(set(new Character('a'), new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("A").getFollowSet());
		assertEquals(set(new Character('d'), EOF.getInstance()), grammar.getNonterminalByName("S").getFollowSet());
	}
	
	@Test
	public void testParsers() {
		NonterminalSymbolNode sppf1 = rdParser.parse("aad", grammar, "S");
		ToJavaCode visitAction = new ToJavaCode();
		sppf1.accept(visitAction);
		System.out.println(visitAction.getString());
		generateGraph(sppf1);
		NonterminalSymbolNode sppf2 = levelParser.parse("aad", grammar, "S");
		assertEquals(true, sppf1.deepEquals(sppf2));
	}

	@Test
	public void testSPPF() {
		NonterminalSymbolNode sppf = rdParser.parse("aad", grammar, "S");
		assertEquals(true, sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		PackedNode node2 = new PackedNode(grammar.getGrammarSlotByName("S ::= [a] S ."), 1, node1);
		TerminalSymbolNode node3 = new TerminalSymbolNode(97, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 1, 2);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TerminalSymbolNode node7 = new TerminalSymbolNode(97, 1);
		node6.addChild(node7);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 2);
		TerminalSymbolNode node9 = new TerminalSymbolNode(-2, 2);
		node8.addChild(node9);
		node5.addChild(node6);
		node5.addChild(node8);
		TerminalSymbolNode node10 = new TerminalSymbolNode(100, 2);
		node4.addChild(node5);
		node4.addChild(node10);
		node2.addChild(node3);
		node2.addChild(node4);
		PackedNode node11 = new PackedNode(grammar.getGrammarSlotByName("S ::= A S [d] ."), 2, node1);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= A S . [d]"), 0, 2);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		node13.addChild(node3);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 2);
		node14.addChild(node7);
		node14.addChild(node8);
		node12.addChild(node13);
		node12.addChild(node14);
		node11.addChild(node12);
		node11.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}
	
}
