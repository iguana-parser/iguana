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
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.datadependent.ast.AST.*;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * X ::= S(1,2)
 * 
 * S(a,b) ::= l:A print(l,a,b) B
 * A ::= 'a'
 * B ::= 'b'
 *
 */

//public class Test1 {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//
//		Nonterminal X = Nonterminal.withName("X");
//
//		Nonterminal S = Nonterminal.builder("S").addParameters("a", "b").build();
//		Nonterminal A = Nonterminal.withName("A");
//		Nonterminal B = Nonterminal.withName("B");
//
//
//		Rule r0 = Rule.withHead(X).addSymbol(Nonterminal.builder(S).apply(integer(1), integer(2)).build()).build();
//
//		Rule r1 = Rule.withHead(S)
//					.addSymbol(Code.code(Nonterminal.builder(A).setLabel("l").setVariable("x").build(),
//											stat(println(var("l"), var("a"), var("b")))))
//					.addSymbol(B).build();
//
//		Rule r2 = Rule.withHead(A).addSymbol(Terminal.from(Char.from('a'))).build();
//		Rule r3 = Rule.withHead(B).addSymbol(Terminal.from(Char.from('b'))).build();
//
//		grammar = Grammar.builder().addRules(r0, r1, r2, r3).build();
//
//	}
//
//	@Test
//	public void test() {
//		System.out.println(grammar);
//
//		Input input = Input.fromString("ab");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//}
