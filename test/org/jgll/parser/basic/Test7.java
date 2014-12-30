package org.jgll.parser.basic;

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
 * A ::= B C D
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'd'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test7 {

	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Nonterminal D = Nonterminal.withName("D");
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	private Character d = Character.from('d');
	
	@Before
	public void init() {
		Rule r1 = Rule.builder(A).addSymbols(B, C, D).build();
		Rule r2 = Rule.builder(B).addSymbol(b).build();
		Rule r3 = Rule.builder(C).addSymbol(c).build();
		Rule r4 = Rule.builder(D).addSymbol(d).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
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
	
	public void testGenerated() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("bc"), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("bcd");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}
	
	private SPPFNode getExpectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= B C D .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B C . D", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= B C . D", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("B ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("C", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("C ::= c .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("D", 0, 2, 3).init();
		PackedNode node12 = factory.createPackedNode("D ::= d .", 3, node11);
		TerminalNode node13 = factory.createTerminalNode("d", 2, 3);
		node12.addChild(node13);
		node11.addChild(node12);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
}