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
 * A ::= B
 * B ::= 'b'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test5 {

	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Character b = Character.from('b');
	
	@Before
	public void init() {
		Rule r1 = Rule.builder(A).addSymbols(B).build();
		Rule r2 = Rule.builder(B).addSymbol(b).build();
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
		assertFalse(grammar.isNullable(B));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	public void testGenerated() {
		StringWriter writer = new StringWriter();
		Input input = Input.fromString("b");
		grammar.toGrammarGraph(input).generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(input, grammar, "A");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("b");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}
	
	private SPPFNode getExpectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node2 = factory.createPackedNode("A ::= B .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("B", 0, 0, 1).init();
		PackedNode node4 = factory.createPackedNode("B ::= b .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
	
}