package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
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

import com.google.common.truth.codegen.CompilingClassLoader;

/**
 * 
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 * 
 */
public class Test2 {

	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	private Character a = Character.from('a');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(a));
		grammar = new Grammar.Builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(A));
	}
	
	@Test
	public void test() {
		StringWriter writer = new StringWriter();
		grammar.toGrammarGraph().generate("test", "Test", new PrintWriter(writer));
		Class<?> clazz = CompilationUtil.getClass("test", "Test", writer.toString());
		
		Input input = Input.fromString("a");
		try {
			GLLParser parser = (GLLParser) clazz.newInstance();
			Method parseMethod = clazz.getMethod("parse", new Class[] {Input.class, GrammarGraph.class, String.class});
			ParseResult result = (ParseResult) parseMethod.invoke(parser, input, grammar.toGrammarGraph(), "A");
			assertTrue(result.isParseSuccess());
			assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF()));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node2 = factory.createPackedNode("A ::= a .", 0, node1);
		TokenSymbolNode node3 = factory.createTokenNode("a", 0, 1);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}

}