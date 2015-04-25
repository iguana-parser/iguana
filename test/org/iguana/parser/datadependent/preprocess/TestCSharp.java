package org.iguana.parser.datadependent.preprocess;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.NonterminalNode;
import org.iguana.traversal.NonterminalNodeVisitor;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
