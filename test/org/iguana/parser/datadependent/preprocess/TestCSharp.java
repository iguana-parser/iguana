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

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.sppf.NonterminalNode;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

public class TestCSharp {
	
	private static Grammar originalGrammar;

	static {
		try {
			originalGrammar = Grammar.load(new File("grammars/csharp/csharp"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar));
	private static Nonterminal start = Nonterminal.withName("CompilationUnit");
	
//	@Test
//	public void test1() throws Exception {
//		Input input = Input.fromFile(new File(getClass().getResource("examples/Test1.cs").getPath()));
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);

//		Map<String, NonterminalNode> nodes = new HashMap<>();
//
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getIndex());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getResult());
		
	//}
	
//	@Test
//	public void test2() throws Exception {
//		Input input = Input.fromFile(new File(getClass().getResource("examples/Test2.cs").getPath()));
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//
//		Map<String, NonterminalNode> nodes = new HashMap<>();
		
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getIndex());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getResult());
		

//		assertTrue(nodes.isEmpty());
	// }

//	@Test
//	public void test3() throws Exception {
//		Input input = Input.fromFile(new File(getClass().getResource("examples/Test7.cs").getPath()));
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);

//		Map<String, NonterminalNode> nodes = new HashMap<>();
//
//		NonterminalNodeVisitor.create(n -> {
//			if (n.getGrammarSlot().getNonterminal().getName().equals("DPpConditional") ||
//				n.getGrammarSlot().getNonterminal().getName().equals("PpConditional")) {
//				String yield = input.subString(n.getLeftExtent(), n.getIndex());
//				nodes.put(yield, n);
//			}
//		}).visit(result.asParseSuccess().getResult());

//	}
	
}
