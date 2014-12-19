package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
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
import org.jgll.util.ToJavaCode;
import org.jgll.util.Visualization;
import org.jgll.util.generator.CompilationUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= A B C D
 * A ::= 'a' | epsilon
 * B ::= 'a' | epsilon
 * C ::= 'a' | epsilon
 * D ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 */
public class Test13 {

	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Nonterminal D = Nonterminal.withName("D");
	private Character a = Character.from('a');
	
	@Before
	public void init() {
		Rule r1 = new Rule(S, list(A, B, C, D));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(A);
		Rule r4 = new Rule(B, list(a));
		Rule r5 = new Rule(B);
		Rule r6 = new Rule(C, list(a));
		Rule r7 = new Rule(C);
		Rule r8 = new Rule(D, list(a));
		Rule r9 = new Rule(D);

		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).
													   addRule(r4).addRule(r5).addRule(r6).
													   addRule(r7).addRule(r8).addRule(r9).build();
	}
	
	@Test
	public void testNullable() {
		assertTrue(grammar.isNullable(S));
		assertTrue(grammar.isNullable(A));
		assertTrue(grammar.isNullable(B));
		assertTrue(grammar.isNullable(C));
		assertTrue(grammar.isNullable(D));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), parser.getRegistry(), input);
		System.out.println(ToJavaCode.toJavaCode(result.asParseSuccess().getRoot()));
		assertEquals(3, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("a"), grammar.toGrammarGraph(), "S");
    	assertTrue(result.isParseSuccess());
    	assertEquals(3, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	private SPPFNode getSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 1).init();
		PackedNode node2 = factory.createPackedNode("S ::= A B C D .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A B C . D", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= A B C . D", 1, node3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= A B . C D", 0, 1).init();
		PackedNode node6 = factory.createPackedNode("S ::= A B . C D", 1, node5);
		NonterminalNode node7 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node8 = factory.createPackedNode("A ::= a .", 0, node7);
		TerminalNode node9 = factory.createTerminalNode("a", 0, 1);
		node8.addChild(node9);
		node7.addChild(node8);
		NonterminalNode node10 = factory.createNonterminalNode("B", 0, 1, 1).init();
		PackedNode node11 = factory.createPackedNode("B ::= .", 1, node10);
		TerminalNode node12 = factory.createEpsilonNode(1);
		node11.addChild(node12);
		node10.addChild(node11);
		node6.addChild(node7);
		node6.addChild(node10);
		PackedNode node13 = factory.createPackedNode("S ::= A B . C D", 0, node5);
		NonterminalNode node14 = factory.createNonterminalNode("A", 0, 0, 0).init();
		PackedNode node15 = factory.createPackedNode("A ::= .", 0, node14);
		TerminalNode node16 = factory.createEpsilonNode(0);
		node15.addChild(node16);
		node14.addChild(node15);
		NonterminalNode node17 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node18 = factory.createPackedNode("B ::= a .", 0, node17);
		node18.addChild(node9);
		node17.addChild(node18);
		node13.addChild(node14);
		node13.addChild(node17);
		node5.addChild(node6);
		node5.addChild(node13);
		NonterminalNode node20 = factory.createNonterminalNode("C", 0, 1, 1).init();
		PackedNode node21 = factory.createPackedNode("C ::= .", 1, node20);
		node21.addChild(node12);
		node20.addChild(node21);
		node4.addChild(node5);
		node4.addChild(node20);
		PackedNode node23 = factory.createPackedNode("S ::= A B C . D", 0, node3);
		IntermediateNode node24 = factory.createIntermediateNode("S ::= A B . C D", 0, 0).init();
		PackedNode node25 = factory.createPackedNode("S ::= A B . C D", 0, node24);
		NonterminalNode node27 = factory.createNonterminalNode("B", 0, 0, 0).init();
		PackedNode node28 = factory.createPackedNode("B ::= .", 0, node27);
		node28.addChild(node16);
		node27.addChild(node28);
		node25.addChild(node14);
		node25.addChild(node27);
		node24.addChild(node25);
		NonterminalNode node30 = factory.createNonterminalNode("C", 0, 0, 1).init();
		PackedNode node31 = factory.createPackedNode("C ::= a .", 0, node30);
		node31.addChild(node9);
		node30.addChild(node31);
		node23.addChild(node24);
		node23.addChild(node30);
		node3.addChild(node4);
		node3.addChild(node23);
		NonterminalNode node33 = factory.createNonterminalNode("D", 0, 1, 1).init();
		PackedNode node34 = factory.createPackedNode("D ::= .", 1, node33);
		node34.addChild(node12);
		node33.addChild(node34);
		node2.addChild(node3);
		node2.addChild(node33);
		PackedNode node36 = factory.createPackedNode("S ::= A B C D .", 0, node1);
		IntermediateNode node37 = factory.createIntermediateNode("S ::= A B C . D", 0, 0).init();
		PackedNode node38 = factory.createPackedNode("S ::= A B C . D", 0, node37);
		NonterminalNode node40 = factory.createNonterminalNode("C", 0, 0, 0).init();
		PackedNode node41 = factory.createPackedNode("C ::= .", 0, node40);
		node41.addChild(node16);
		node40.addChild(node41);
		node38.addChild(node39);
		node38.addChild(node40);
		node37.addChild(node38);
		NonterminalNode node43 = factory.createNonterminalNode("D", 0, 0, 1).init();
		PackedNode node44 = factory.createPackedNode("D ::= a .", 0, node43);
		node44.addChild(node9);
		node43.addChild(node44);
		node36.addChild(node37);
		node36.addChild(node43);
		node1.addChild(node2);
		node1.addChild(node36);
		return node1;
	}
}
