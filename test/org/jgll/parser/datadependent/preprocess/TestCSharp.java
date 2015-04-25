package org.jgll.parser.datadependent.preprocess;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.traversal.NonterminalNodeVisitor;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Test;

public class TestCSharp {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/csharp/csharp"));
	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar));
	private static Start start = Start.from(Nonterminal.withName("CompilationUnit"));
	
	@Test
	public void test1() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test1.cs").getPath());
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, start);
		System.out.println(result);
		
		Map<String, NonterminalNode> nodes = new HashMap<>();
		
		NonterminalNodeVisitor.create(n -> {
			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
				nodes.put(yield, n);
			}
		}).visit(result.asParseSuccess().getRoot());
		

		assertTrue(result.isParseSuccess());
		assertTrue(nodes.isEmpty());
	}
	
	@Test
	public void test2() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test2.cs").getPath());
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, start);
		System.out.println(result);
		
		Map<String, NonterminalNode> nodes = new HashMap<>();
		
		NonterminalNodeVisitor.create(n -> {
			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
				nodes.put(yield, n);
			}
		}).visit(result.asParseSuccess().getRoot());
		

		assertTrue(result.isParseSuccess());
//		assertTrue(nodes.isEmpty());
	}

	@Test
	public void test3() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test3.cs").getPath());
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, start);
		System.out.println(result);
		
		Map<String, NonterminalNode> nodes = new HashMap<>();
		
		NonterminalNodeVisitor.create(n -> {
			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
				nodes.put(yield, n);
			}
		}).visit(result.asParseSuccess().getRoot());
		

		assertTrue(result.isParseSuccess());
//		assertTrue(nodes.isEmpty());
	}
	
}
