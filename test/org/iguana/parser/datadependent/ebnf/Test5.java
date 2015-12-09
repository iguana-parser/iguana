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

package org.iguana.parser.datadependent.ebnf;

import static org.iguana.datadependent.ast.AST.greaterEq;
import static org.iguana.datadependent.ast.AST.lExt;
import static org.iguana.datadependent.ast.AST.println;
import static org.iguana.datadependent.ast.AST.rExt;
import static org.iguana.datadependent.ast.AST.stat;
import static org.iguana.datadependent.ast.AST.var;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.Character;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * @author Anastasia Izmaylova
 *
 * X ::= a:A b:(b:B [b.lExt >= a.rExt] print(b.lExt) c:C print(c))*  // shadowing (b:B)
 * 
 * A ::= a
 * B ::= b
 * C ::= c
 *
 */

public class Test5 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		Rule r1 = Rule.withHead(X)
					.addSymbol(Nonterminal.builder(A).setLabel("a").build())
					.addSymbol(Star.builder(Sequence.builder(Code.code(Nonterminal.builder(B).setLabel("b")
																			.addPreCondition(predicate(greaterEq(lExt("b"), rExt("a")))).build(),
																	   stat(println(lExt("b")))),
															 
															 Code.code(Nonterminal.builder(C).setLabel("c").build(),
																	   stat(println(var("c"))))).build())
									.setLabel("b").build()).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Terminal.from(Character.from('a'))).build();
		Rule r3 = Rule.withHead(B).addSymbol(Terminal.from(Character.from('b'))).build();
		Rule r4 = Rule.withHead(C).addSymbol(Terminal.from(Character.from('c'))).build();
		
		grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		grammar = new EBNFToBNF().transform(grammar);
		System.out.println(grammar);
		
		Input input = Input.fromString("abcbcbc");
		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
		
		// Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("X"));
		
		Assert.assertTrue(result.isParseSuccess());
		
		// Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
		// 		result.asParseSuccess().getSPPFNode(), input);
		
		Assert.assertTrue(result.asParseSuccess().getStatistics().getCountAmbiguousNodes() == 0);
	}

}
