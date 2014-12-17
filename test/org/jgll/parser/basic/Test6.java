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
import org.jgll.util.generator.CompilationUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test6 {

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(a, B, c));
		Rule r2 = new Rule(A, list(C));
		Rule r3 = new Rule(B, list(b));
		Rule r4 = new Rule(C, list(a, C));
		Rule r5 = new Rule(C, list(c));
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
		assertFalse(grammar.isNullable(B));
		assertFalse(grammar.isNullable(C));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	@Test
	public void testGenerated1() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("abc"), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1(parser.getRegistry())));
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF1(parser.getRegistry())));
	}
	
	@Test
	public void testParser2() {
		Input input = Input.fromString("aaaac");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF2(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated2() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("aaaac"), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF2(parser.getRegistry())));
	}
	
	private SPPFNode getSPPF1(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= a B c .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= a B . c", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= a B . c", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("B", 1, 2).init();
		PackedNode node7 = factory.createPackedNode("B ::= b .", 1, node6);
		TerminalNode node8 = factory.createTerminalNode("b", 1, 2);
		node7.addChild(node8);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node9 = factory.createTerminalNode("c", 2, 3);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}
	
	private SPPFNode getSPPF2(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 5).init();
		PackedNode node2 = factory.createPackedNode("A ::= C .", 0, node1);
		NonterminalNode node3 = factory.createNonterminalNode("C", 0, 5).init();
		PackedNode node4 = factory.createPackedNode("C ::= a C .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("C", 1, 5).init();
		PackedNode node7 = factory.createPackedNode("C ::= a C .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
		NonterminalNode node9 = factory.createNonterminalNode("C", 2, 5).init();
		PackedNode node10 = factory.createPackedNode("C ::= a C .", 3, node9);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 3);
		NonterminalNode node12 = factory.createNonterminalNode("C", 3, 5).init();
		PackedNode node13 = factory.createPackedNode("C ::= a C .", 4, node12);
		TerminalNode node14 = factory.createTerminalNode("a", 3, 4);
		NonterminalNode node15 = factory.createNonterminalNode("C", 4, 5).init();
		PackedNode node16 = factory.createPackedNode("C ::= c .", 4, node15);
		TerminalNode node17 = factory.createTerminalNode("c", 4, 5);
		node16.addChild(node17);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		node12.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node12);
		node9.addChild(node10);
		node7.addChild(node8);
		node7.addChild(node9);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
	
}
	