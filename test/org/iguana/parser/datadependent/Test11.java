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

import iguana.regex.Alt;
import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.datadependent.ast.AST.*;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * X ::= S
 * 
 * @layout(NoNL)
 * S ::= a:'a' [a.lExt == 0] print(a.rExt, indent(a.rExt)) b:'b' [b.lExt == 5] print(b.rExt, indent(b.rExt))
 *
 */

//public class Test11 {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//
//		Nonterminal X = Nonterminal.withName("X");
//
//		Nonterminal NoNL = Nonterminal.withName("NoNL");
//
//		Nonterminal S = Nonterminal.withName("S");
//
//		Rule r0 = Rule.withHead(X).addSymbol(S).build();
//
//		Rule r1 = Rule.withHead(S)
//					.addSymbol(Code.code(Terminal.builder(Char.from('a')).setLabel("a")
//											.addPreCondition(predicate(equal(lExt("a"), integer(0)))).build(),
//										 stat(println(rExt("a"), indent(rExt("a"))))))
//					.addSymbol(NoNL) // TODO: Should be removed
//					.addSymbol(Code.code(Terminal.builder(Char.from('b')).setLabel("b")
//												.addPreCondition(predicate(equal(lExt("b"), integer(5)))).build(),
//										 stat(println(rExt("b"), indent(rExt("b"))))))
//
//					.setLayout(NoNL).setLayoutStrategy(LayoutStrategy.FIXED).build();
//
//		Rule r2 = Rule.withHead(Nonterminal.builder("NoNL").build())
//						.addSymbol(Star.builder(Terminal.from(Alt.from(Char.from(' '), Char.from('\t'))))
//								.addPostCondition(RegularExpressionCondition.notFollow(Char.from(' ')))
//								.addPostCondition(RegularExpressionCondition.notFollow(Char.from('\t'))).build()).build();
//
//		grammar = Grammar.builder().addRules(r0, r1, r2).build();
//
//	}
//
//	@Test
//	public void test() {
//		System.out.println(grammar);
//
//		grammar = new EBNFToBNF().transform(grammar);
//
//		Input input = Input.fromString("a    b");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}
//
//}
