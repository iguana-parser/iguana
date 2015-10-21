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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Test;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.utils.input.Input;

public class TestCSharp {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/csharp/csharp"));
	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar));
	private static Start start = Start.from(Nonterminal.withName("CompilationUnit"));
	
	@Test
	public void test1() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test1.cs").getPath());
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, start);
		System.out.println(result);
		
//		Map<String, NonterminalNode> nodes = new HashMap<>();
//
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getSPPFNode());
		

		assertTrue(result.isParseSuccess());
//		assertTrue(nodes.isEmpty());
	}
	
	@Test
	public void test2() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test2.cs").getPath());
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, start);
		System.out.println(result);

		Map<String, NonterminalNode> nodes = new HashMap<>();
		
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getSPPFNode());
		

		assertTrue(result.isParseSuccess());
//		assertTrue(nodes.isEmpty());
	}

	@Test
	public void test3() throws Exception {
		Input input = Input.fromPath(getClass().getResource("examples/Test7.cs").getPath());
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, start);
		System.out.println(result);
		
//		Map<String, NonterminalNode> nodes = new HashMap<>();
//
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getSPPFNode());
		

		assertTrue(result.isParseSuccess());
//		assertTrue(nodes.isEmpty());
	}
	
}
