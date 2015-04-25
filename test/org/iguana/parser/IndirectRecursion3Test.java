package org.iguana.parser;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
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
 * A ::= B c
 *     | C d
 *     | e
 * 
 * B ::= A f
 *     | A g
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion3Test {
	
	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Character c = Character.from('c');
	private Character d = Character.from('d');
	private Character e = Character.from('e');
	private Character f = Character.from('f');
	private Character g = Character.from('g');

	@Before
	public void init() {
		Rule r1 = Rule.withHead(A).addSymbols(B, c).build();
		Rule r2 = Rule.withHead(A).addSymbols(C, d).build();
		Rule r3 = Rule.withHead(A).addSymbols(e).build();
		Rule r4 = Rule.withHead(B).addSymbols(A, f).build();
		Rule r5 = Rule.withHead(C).addSymbols(A, g).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3)
								    .addRule(r4).addRule(r5).build();
	}
	
//	@Test
//	public void testNullable() {
//		assertFalse(grammar.isNullable(A));
//		assertFalse(grammar.isNullable(B));
//	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("efcfc");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode1(parser.getGrammarGraph())));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("egdgdgd");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode2(parser.getGrammarGraph())));		
	}
	
	@Test
	public void test3() {
		Input input = Input.fromString("egdfcgd");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("A"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPFNode3(parser.getGrammarGraph())));
	}
	
	private SPPFNode getSPPFNode1(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 5);
		PackedNode node2 = factory.createPackedNode("A ::= B c .", 4, node1);
		NonterminalNode node3 = factory.createNonterminalNode("B", 0, 0, 4);
		PackedNode node4 = factory.createPackedNode("B ::= A f .", 3, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 3);
		PackedNode node6 = factory.createPackedNode("A ::= B c .", 2, node5);
		NonterminalNode node7 = factory.createNonterminalNode("B", 0, 0, 2);
		PackedNode node8 = factory.createPackedNode("B ::= A f .", 1, node7);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node10 = factory.createPackedNode("A ::= e .", 1, node9);
		TerminalNode node11 = factory.createTerminalNode("e", 0, 1);
		node10.addChild(node11);
		node9.addChild(node10);
		TerminalNode node12 = factory.createTerminalNode("f", 1, 2);
		node8.addChild(node9);
		node8.addChild(node12);
		node7.addChild(node8);
		TerminalNode node13 = factory.createTerminalNode("c", 2, 3);
		node6.addChild(node7);
		node6.addChild(node13);
		node5.addChild(node6);
		TerminalNode node14 = factory.createTerminalNode("f", 3, 4);
		node4.addChild(node5);
		node4.addChild(node14);
		node3.addChild(node4);
		TerminalNode node15 = factory.createTerminalNode("c", 4, 5);
		node2.addChild(node3);
		node2.addChild(node15);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode2(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 7);
		PackedNode node2 = factory.createPackedNode("A ::= C d .", 6, node1);
		NonterminalNode node3 = factory.createNonterminalNode("C", 0, 0, 6);
		PackedNode node4 = factory.createPackedNode("C ::= A g .", 5, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 5);
		PackedNode node6 = factory.createPackedNode("A ::= C d .", 4, node5);
		NonterminalNode node7 = factory.createNonterminalNode("C", 0, 0, 4);
		PackedNode node8 = factory.createPackedNode("C ::= A g .", 3, node7);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0, 3);
		PackedNode node10 = factory.createPackedNode("A ::= C d .", 2, node9);
		NonterminalNode node11 = factory.createNonterminalNode("C", 0, 0, 2);
		PackedNode node12 = factory.createPackedNode("C ::= A g .", 1, node11);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node14 = factory.createPackedNode("A ::= e .", 1, node13);
		TerminalNode node15 = factory.createTerminalNode("e", 0, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node16 = factory.createTerminalNode("g", 1, 2);
		node12.addChild(node13);
		node12.addChild(node16);
		node11.addChild(node12);
		TerminalNode node17 = factory.createTerminalNode("d", 2, 3);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		TerminalNode node18 = factory.createTerminalNode("g", 3, 4);
		node8.addChild(node9);
		node8.addChild(node18);
		node7.addChild(node8);
		TerminalNode node19 = factory.createTerminalNode("d", 4, 5);
		node6.addChild(node7);
		node6.addChild(node19);
		node5.addChild(node6);
		TerminalNode node20 = factory.createTerminalNode("g", 5, 6);
		node4.addChild(node5);
		node4.addChild(node20);
		node3.addChild(node4);
		TerminalNode node21 = factory.createTerminalNode("d", 6, 7);
		node2.addChild(node3);
		node2.addChild(node21);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPFNode3(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 7);
		PackedNode node2 = factory.createPackedNode("A ::= C d .", 6, node1);
		NonterminalNode node3 = factory.createNonterminalNode("C", 0, 0, 6);
		PackedNode node4 = factory.createPackedNode("C ::= A g .", 5, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 5);
		PackedNode node6 = factory.createPackedNode("A ::= B c .", 4, node5);
		NonterminalNode node7 = factory.createNonterminalNode("B", 0, 0, 4);
		PackedNode node8 = factory.createPackedNode("B ::= A f .", 3, node7);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0, 3);
		PackedNode node10 = factory.createPackedNode("A ::= C d .", 2, node9);
		NonterminalNode node11 = factory.createNonterminalNode("C", 0, 0, 2);
		PackedNode node12 = factory.createPackedNode("C ::= A g .", 1, node11);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node14 = factory.createPackedNode("A ::= e .", 1, node13);
		TerminalNode node15 = factory.createTerminalNode("e", 0, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		TerminalNode node16 = factory.createTerminalNode("g", 1, 2);
		node12.addChild(node13);
		node12.addChild(node16);
		node11.addChild(node12);
		TerminalNode node17 = factory.createTerminalNode("d", 2, 3);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		TerminalNode node18 = factory.createTerminalNode("f", 3, 4);
		node8.addChild(node9);
		node8.addChild(node18);
		node7.addChild(node8);
		TerminalNode node19 = factory.createTerminalNode("c", 4, 5);
		node6.addChild(node7);
		node6.addChild(node19);
		node5.addChild(node6);
		TerminalNode node20 = factory.createTerminalNode("g", 5, 6);
		node4.addChild(node5);
		node4.addChild(node20);
		node3.addChild(node4);
		TerminalNode node21 = factory.createTerminalNode("d", 6, 7);
		node2.addChild(node3);
		node2.addChild(node21);
		node1.addChild(node2);
		return node1;
	}
	
}