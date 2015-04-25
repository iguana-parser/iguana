package org.jgll.parser.datadependent.preprocess;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class TestPreprocessor2 {

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Preprocessor.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("A"));
	
	@Test
	public void test1() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example2.pp").getPath());
		
			Map<String, Object> variables = ImmutableMap.<String, Object>builder()
					.put("v1", true)
					.put("v2", true)
					.put("v3", true)
					.put("v4", false)
					.put("v5", true)
					.put("v6", true)
					.put("v7", true)
					.put("v8", true)
					.put("v9", true)
					.build();
			Set<String> expected = ImmutableSet.of("v1", "v2", "v3", "v5");
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, start, variables);

			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			assertEquals(expected, nodes.keySet());
		}		

	@Test
	public void test2() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example2.pp").getPath());
		
			Map<String, Object> variables = ImmutableMap.<String, Object>builder()
					.put("v1", true)
					.put("v2", true)
					.put("v3", true)
					.put("v4", false)
					.put("v5", false)
					.put("v6", false)
					.put("v7", true)
					.put("v8", true)
					.put("v9", true)
					.build();
			Set<String> expected = ImmutableSet.of("v1", "v2", "v3", "nv4", "nv5", "nv6");
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, start, variables);

			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			assertEquals(expected, nodes.keySet());
		}		

	@Test
	public void test3() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example2.pp").getPath());
		
			Map<String, Object> variables = ImmutableMap.<String, Object>builder()
					.put("v1", true)
					.put("v2", false)
					.put("v3", true)
					.put("v4", true)
					.put("v5", true)
					.put("v6", true)
					.put("v7", false)
					.put("v8", false)
					.put("v9", true)
					.build();
			Set<String> expected = ImmutableSet.of("v1", "v9", "nv2");
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, start, variables);

			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			assertEquals(expected, nodes.keySet());
		}	
	
	@Test
	public void test4() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example2.pp").getPath());
		
			Map<String, Object> variables = ImmutableMap.<String, Object>builder()
					.put("v1", true)
					.put("v2", false)
					.put("v3", true)
					.put("v4", true)
					.put("v5", true)
					.put("v6", true)
					.put("v7", false)
					.put("v8", false)
					.put("v9", false)
					.build();
			Set<String> expected = ImmutableSet.of("v1", "nv2");
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, start, variables);

			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			assertEquals(expected, nodes.keySet());
		}
	
	
}
