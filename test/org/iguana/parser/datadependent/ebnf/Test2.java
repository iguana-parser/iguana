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

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.datadependent.ast.AST.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 
 * @author Anastasia Izmaylova
 *
 * X ::= A s:(b:B | c:C print(s.lExt) d:D)*
 * 
 * A ::= a
 * B ::= b
 * C ::= c
 * D ::= d
 *
 */

public class Test2 {
	
	private RuntimeGrammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Nonterminal D = Nonterminal.withName("D");
		
		
		RuntimeRule r1 = RuntimeRule.withHead(X)
					.addSymbol(new Nonterminal.Builder(A).build())
					.addSymbol(new Star.Builder(new Alt.Builder(new Nonterminal.Builder(B).setLabel("b").build(),
														new Group.Builder(Code.code(new Nonterminal.Builder(C).setLabel("c").build(),
																				   stat(println(lExt("s")))),
																         new Nonterminal.Builder(D).setLabel("d").build()).build()).build())
									.setLabel("s").build()).build();
		
		RuntimeRule r2 = RuntimeRule.withHead(A).addSymbol(Terminal.from(Char.from('a'))).build();
		RuntimeRule r3 = RuntimeRule.withHead(B).addSymbol(Terminal.from(Char.from('b'))).build();
		RuntimeRule r4 = RuntimeRule.withHead(C).addSymbol(Terminal.from(Char.from('c'))).build();
		RuntimeRule r5 = RuntimeRule.withHead(D).addSymbol(Terminal.from(Char.from('d'))).build();
		
		grammar = RuntimeGrammar.builder().addRules(r1, r2, r3, r4, r5).build();
		
	}
	
	@Test
	public void test() {
		grammar = new EBNFToBNF().transform(grammar);

		Input input = Input.fromString("acdbcd");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
	}

}
