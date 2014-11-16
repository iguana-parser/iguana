package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
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
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.generator.CompilationUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Test10 {
	
	private Nonterminal A = Nonterminal.withName("A");
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(Character.from('a')));
		Rule r3 = new Rule(A);

		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		
		// TODO: stackoverflow bug due to the cycle in the SPPF. Fix it later!
//		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void testGenerated1() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString(""), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
	}
	
	@Test
	public void testGenerated2() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("a"), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
	}
	
	private SPPFNode getSPPF1() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node2 = factory.createPackedNode("A ::= a .", 0, node1);
		TokenSymbolNode node3 = factory.createTokenNode("a", 0, 1);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 1, node1);
		NonterminalNode node5 = factory.createNonterminalNode("A", 1, 1).init();
		PackedNode node6 = factory.createPackedNode("A ::= .", 1, node5);
		PackedNode node7 = factory.createPackedNode("A ::= A A .", 1, node5);
		node7.addChild(node5);
		node7.addChild(node5);
		node5.addChild(node6);
		node5.addChild(node7);
		node4.addChild(node1);
		node4.addChild(node5);
		PackedNode node8 = factory.createPackedNode("A ::= A A .", 0, node1);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0).init();
		PackedNode node10 = factory.createPackedNode("A ::= .", 0, node9);
		PackedNode node11 = factory.createPackedNode("A ::= A A .", 0, node9);
		node11.addChild(node9);
		node11.addChild(node9);
		node9.addChild(node10);
		node9.addChild(node11);
		node8.addChild(node9);
		node8.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node4);
		node1.addChild(node8);
		return node1;
	}
}
