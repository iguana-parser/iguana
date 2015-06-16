/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.parser.datadependent.preprocess;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import static org.iguana.util.CollectionsUtil.*;

public class TestPreprocessor2 {

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Preprocessor.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("A"));
	
	@Test
	public void test1() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example2.pp").getPath());
		
		Map<String, Boolean> variables = map(list(tuple("v1", true),
												  tuple("v2", true),
												  tuple("v3", true),
												  tuple("v4", false),
												  tuple("v5", true),
												  tuple("v6", true),
												  tuple("v7", true),
												  tuple("v8", true),
												  tuple("v9", true)));
		
		Set<String> expected = set("v1", "v2", "v3", "v5");
		
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
		
		Map<String, Boolean> variables = map(list(tuple("v1", true),
												  tuple("v2", true),
												  tuple("v3", true),
												  tuple("v4", false),
												  tuple("v5", false),
												  tuple("v6", false),
												  tuple("v7", true),
												  tuple("v8", true),
												  tuple("v9", true)));
		
		Set<String> expected = set("v1", "v2", "v3", "nv4", "nv5", "nv6");
		
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
		
		Map<String, Boolean> variables = map(list(tuple("v1", true),
												  tuple("v2", false),
												  tuple("v3", true),
												  tuple("v4", true),
												  tuple("v5", true),
												  tuple("v6", true),
												  tuple("v7", false),
												  tuple("v8", false),
												  tuple("v9", true)));
		
		Set<String> expected = set("v1", "v9", "nv2");
		
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
		
		Map<String, Boolean> variables = map(list(tuple("v1", true),
												  tuple("v2", false),
												  tuple("v3", true),
												  tuple("v4", true),
												  tuple("v5", true),
												  tuple("v6", true),
												  tuple("v7", false),
												  tuple("v8", false),
												  tuple("v9", false)));
			
		Set<String> expected = set("v1", "nv2");
			
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
