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
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test9 {

	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');

	
	@Before
	public void init() {
		
		Rule r1 = new Rule(S, list(A, A, b));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(A);
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertEquals(1, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	@Test
	public void testGenerated() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("ab"), grammar.toGrammarGraph(), "S");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF(parser.getRegistry())));
	}
	
	private SPPFNode getSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2).init();
		PackedNode node2 = factory.createPackedNode("S ::= A A b .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A A . b", 0, 1).init();
		PackedNode node4 = factory.createPackedNode("S ::= A A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 0).init();
		PackedNode node6 = factory.createPackedNode("A ::= .", 0, node5);
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
		PackedNode node11 = factory.createPackedNode("S ::= A A . b", 1, node3);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 1, 1).init();
		PackedNode node14 = factory.createPackedNode("A ::= .", 1, node13);
		TerminalNode node15 = factory.createEpsilonNode(1);
		node14.addChild(node15);
		node13.addChild(node14);
		node11.addChild(node8);
		node11.addChild(node13);
		node3.addChild(node4);
		node3.addChild(node11);
		TerminalNode node16 = factory.createTerminalNode("b", 1, 2);
		node2.addChild(node3);
		node2.addChild(node16);
		node1.addChild(node2);
		return node1;
	}
}
	