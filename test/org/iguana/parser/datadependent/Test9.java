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
 * S ::= E(0,0)
 * 
 * E(l,r) ::=[5 >= r] E(5,r) z // propagate r down
 *         | [4 >= l] x E(l,4) // propagate l down
 *         | [3 >= r] E(3,0) w
 *         | [2 >= l] y E(0,0)
 *         | a
 *
 * tricky inputs: see Test8 and: (x (y a)) w
 */

//public class Test9 {
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
//		Terminal z = Terminal.from(Char.from('z'));
//		Terminal w = Terminal.from(Char.from('w'));
//
//		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
//
//		Rule r1_1 = Rule.withHead(E)
//					.addSymbol(Nonterminal.builder(E).apply(integer(5), var("r"))
//						.addPreCondition(predicate(greaterEq(integer(5), var("r")))).build())
//					.addSymbol(z).build();
//
//		Rule r1_2 = Rule.withHead(E)
//					.addSymbol(Terminal.builder(Char.from('x'))
//							.addPreCondition(predicate(greaterEq(integer(4), var("l")))).build())
//					.addSymbol(Nonterminal.builder(E).apply(var("l"), integer(4)).build()).build();
//
//		Rule r1_3 = Rule.withHead(E)
//					.addSymbol(Nonterminal.builder(E).apply(integer(3), integer(0))
//						.addPreCondition(predicate(greaterEq(integer(3), var("r")))).build())
//					.addSymbol(w).build();
//
//		Rule r1_4 = Rule.withHead(E)
//					.addSymbol(Terminal.builder(Char.from('y'))
//						.addPreCondition(predicate(greaterEq(integer(2), var("l")))).build())
//					.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
//
//		Rule r1_5 = Rule.withHead(E).addSymbol(Terminal.from(Char.from('a'))).build();
//
//		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3, r1_4, r1_5).build();
//
//	}
//
//	@Test
//	public void test() {
//		Input input = Input.fromString("xyaw");
//
//        IguanaParser parser = new IguanaParser(grammar);
//        ParseTreeNode result = parser.getParserTree(input);
//
//        assertNotNull(result);
//	}

// }
