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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.iguana.util.Tuple;
import org.junit.Test;

public class TestPreprocessor {

	private static final Grammar TRANSFORM = new EBNFToBNF().transform(Preprocessor.grammar);

	private static Grammar grammar = new LayoutWeaver().transform(TRANSFORM);
	
	private static Nonterminal start = Start.from(Nonterminal.withName("A"));
	
	@Test
	public void test1() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example1.pp").getPath());
		
		for (Tuple<Map<String, Object>, Set<String>> t : getTestResult()) {
			Map<String, Object> variables = t.getFirst();
			Set<String> expected = t.getSecond();
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			
			// System.out.println(TRANSFORM);
			ParseResult result = parser.parse(input, grammar, start, variables);
			
			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			System.out.println(expected + " ; " + nodes.keySet());
			
			assertEquals(expected, nodes.keySet());
		}		
	}

	private List<Tuple<Map<String, Object>, Set<String>>> getTestResult() {
		
		List<Tuple<Map<String, Object>, Set<String>>> result = new ArrayList<>();
	
		boolean v1, v2, v3, v4, v5;
		
		for (int i1 = 1; i1 <= 2; i1++) {
			v1 = i1 == 1;
			for (int i2 = 1; i2 <= 2; i2++) {
				v2 = i2 == 1;
				for (int i3 = 1; i3 <= 2; i3++) {
					v3 = i3 == 1;
					for (int i4 = 1; i4 <= 2; i4++) {
						v4 = i4 == 1;
						for (int i5 = 1; i5 <= 2; i5++) {
							v5 = i5 == 1;
							
							Map<String, Object> map = new HashMap<>();
							Set<String> set = new HashSet<>();

							map.put("v1", v1);
							map.put("v2", v2);
							map.put("v3", v3);
							map.put("v4", v4);
							map.put("v5", v5);
							
							
							if (v1) {
								set.add("v1");
								if (v2) {
									set.add("v2");
									if (v3) { 
										set.add("v3");
										if (v4) { 
											set.add("v4");
											if (v5) { 
												set.add("v5"); 
											} else { 
												set.add("nv5");
											}
										} else {
											set.add("nv4");
										}
									} else {
										set.add("nv3");
									}
								} else {
									set.add("nv2");
								}
							} else {
								set.add("nv1");
							}
							
							result.add(Tuple.of(map, set));
						}
					}									
				}
			}
		}		
		
		return result;
	}
	
}
