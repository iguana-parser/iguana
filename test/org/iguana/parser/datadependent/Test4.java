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
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
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
 * Operator precedence
 * 
 * l - by left recursive E
 * r - by right recursive E
 * 
 * S ::= E(0,0)
 * 
 * E(l,r) ::= [3 >= l, 3 >= r] E(4,r) '^' E(0,3) // propagate only r to the left as '^' is defined as right associative
 *          | [2 >= r] E(0,0) '-'
 *          | 'a'
 *
 * 1st alternative: 
 * 		E(4,r): right (3 - left), r - can be changed by a parent
 * 		E(0,3): right (4 - left), i.e.,
 * 			  E(0,0)
 *           /   |   \
 *       E(4,0)  ^  E(0,3)
 *                 /   |   \
 *             E(4,3)  ^  E(0,3)
 *
 */

//public class Test4 {
//
//	private Grammar grammar;
//
//	@Before
//	public void init() {
//
//		Nonterminal S = Nonterminal.withName("S");
//
//		Nonterminal E = Nonterminal.builder("E").addParameters("l", "r").build();
//
//		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
//
//		Rule r1_1 = Rule.withHead(E)
//					.addSymbol(Nonterminal.builder(E).apply(integer(4), var("r"))
//							.addPreCondition(predicate(greaterEq(integer(3), var("l"))))
//							.addPreCondition(predicate(greaterEq(integer(3), var("r")))).build())
//					.addSymbol(Terminal.from(Char.from('^')))
//					.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(3)).build()).build();
//
//		Rule r1_2 = Rule.withHead(E)
//				.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0))
//						.addPreCondition(predicate(greaterEq(integer(2), var("r")))).build())
//				.addSymbol(Terminal.from(Char.from('-'))).build();
//
//		Rule r1_3 = Rule.withHead(E).addSymbol(Terminal.from(Char.from('a'))).build();
//
//		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3).build();
//
//	}
//
//	@Test
//	public void test() {
//		System.out.println(grammar);
//
//		 Input input = Input.fromString("a^a-^a");
//		// Input input = Input.fromString("a^a^a");
//		// Input input = Input.fromString("a^a-");
//		// Input input = Input.fromString("a-^a");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//    }
//
//}
