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
 * 
 * A ::= 'a' 'b' 'c'
 * 
 * @author Ali Afroozeh
 */
public class Test4 {

	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	@Before
	public void init() {
		Rule r1 = Rule.builder(A).addSymbols(a, b, c).build();
		grammar = new Grammar.Builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
	}
	
	public void testParser() {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF(parser.getRegistry())));
	}
	
	public void testGenerated() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate(new PrintWriter(writer));
		GLLParser parser = CompilationUtil.getParser(writer.toString());
		ParseResult result = parser.parse(Input.fromString("abc"), grammar.toGrammarGraph(), "A");
    	assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF(parser.getRegistry())));
	}
	
	private SPPFNode expectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("A ::= a b c .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= a b . c", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("A ::= a b . c", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		TerminalNode node6 = factory.createTerminalNode("b", 1, 2);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node7 = factory.createTerminalNode("c", 2, 3);
		node2.addChild(node3);
		node2.addChild(node7);
		node1.addChild(node2);
		return node1;
	}

}