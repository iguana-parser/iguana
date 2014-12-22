package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
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
 * A ::= B A d | a
 * 
 * B ::= epsilon | b
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion2Test {
	
	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character d = Character.from('d');

	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, A, d));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B);
		Rule r4 = new Rule(B, list(b));

		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
		assertTrue(grammar.isNullable(B));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ad");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF(parser.getRegistry())));
	}
	
	//TODO: fix it
	public void testReachabilityGraph() {
//		Set<HeadGrammarSlot> set = builder.getDirectReachableNonterminals("A");
//		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("A")));
//		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("B")));
	}
	
	private SPPFNode expectedSPPF(GrammarSlotRegistry registry) {		
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 2).init();
		PackedNode node2 = factory.createPackedNode("A ::= B A d .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B A . d", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("A ::= B A . d", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("B ::= .", 0, node5);
		TerminalNode node7 = factory.createEpsilonNode(0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node9 = factory.createPackedNode("A ::= a .", 0, node8);
		TerminalNode node10 = factory.createTerminalNode("a", 0, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		TerminalNode node11 = factory.createTerminalNode("d", 1, 2);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}

}