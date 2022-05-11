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

package org.iguana.parser.datadependent;

import iguana.regex.Char;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Terminal;
import org.junit.Before;
import org.junit.Test;

import static org.iguana.datadependent.ast.AST.*;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * Ambiguous
 * 
 * X ::= S(1,2)
 * 
 * S(a,b) ::= l:A print(l,a,b) B
 *          | l1:C l2:A print(l1,l2,a,b) D
 *           
 * A ::= 'a' 'a' | 'a'
 * B ::= 'b' | 'a' 'b'
 * C ::= 'a'
 * D ::= 'b'
 *
 */

public class Test2 {
	
	private RuntimeGrammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal S = new Nonterminal.Builder("S").addParameters("a", "b").build();
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Nonterminal D = Nonterminal.withName("D");
		
		
		RuntimeRule r0 = RuntimeRule.withHead(X).addSymbol(new Nonterminal.Builder(S).apply(integer(1), integer(2)).build()).build();
		
		RuntimeRule r1_1 = RuntimeRule.withHead(S)
					.addSymbol(Code.code(new Nonterminal.Builder(A).setLabel("l").setVariable("x").build(),
										 stat(println(string("PRINT1: "), var("l"), var("a"), var("b")))))
					
					.addSymbol(B).build();
		
		RuntimeRule r1_2 = RuntimeRule.withHead(S)
				.addSymbol(new Nonterminal.Builder(C).setLabel("l1").build())
				.addSymbol(Code.code(new Nonterminal.Builder(A).setLabel("l2").build(),
									 stat(println(string("PRINT2: "), var("l1"), var("l2"), var("a"), var("b")))))
				.addSymbol(D).build();
		
		RuntimeRule r2_1 = RuntimeRule.withHead(A).addSymbol(Terminal.from(Char.from('a'))).addSymbol(Terminal.from(Char.from('a'))).build();
		RuntimeRule r2_2 = RuntimeRule.withHead(A).addSymbol(Terminal.from(Char.from('a'))).build();
		RuntimeRule r3_1 = RuntimeRule.withHead(B).addSymbol(Terminal.from(Char.from('b'))).build();
		RuntimeRule r3_2 = RuntimeRule.withHead(B).addSymbol(Terminal.from(Char.from('a'))).addSymbol(Terminal.from(Char.from('b'))).build();
		
		RuntimeRule r4 = RuntimeRule.withHead(C).addSymbol(Terminal.from(Char.from('a'))).build();
		RuntimeRule r5 = RuntimeRule.withHead(D).addSymbol(Terminal.from(Char.from('b'))).build();
		
		grammar = RuntimeGrammar.builder().addRules(r0, r1_1, r1_2, r2_1, r2_2, r3_1, r3_2, r4, r5).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
// 		FIXME: Graph builder for Code symbol

//		Input input = Input.fromString("aab");
//		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
//		
//		IguanaParser parser = ParserFactory.getParser();
//		ParseResult result = parser.getParserTree(input, graph, Nonterminal.withName("X"));
//		
//		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
//		
//		if (result.isParseSuccess()) {
//			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
//					result.asParseSuccess().getResult(), input);
//		}
		
	}

}
