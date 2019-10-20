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

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * 
 * S ::= E(2)
 * 
 * E(v) ::= [v > 1] 'a' 'b' 'c'
 *        | [v == 0] A B C
 *        
 * A ::= 'a'
 * 
 * B ::= 'b'
 * 
 * C ::= 'c'
 *
 */

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.datadependent.ast.AST.*;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

public class Test3 {
	
	private RuntimeGrammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = new Nonterminal.Builder("E").addParameters("v").build();
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		
		RuntimeRule r0 = RuntimeRule.withHead(S).addSymbol(new Nonterminal.Builder(E).apply(integer(0)).build()).build();
		
		RuntimeRule r1_1 = RuntimeRule.withHead(E)
					.addSymbol(new Terminal.Builder(Char.from('a')).addPreCondition(predicate(greater(var("v"), integer(1)))).build())
					.addSymbol(Terminal.from(Char.from('b'))).addSymbol(Terminal.from(Char.from('c'))).build();
		
		RuntimeRule r1_2 = RuntimeRule.withHead(E)
				.addSymbol(new Nonterminal.Builder(A).addPreCondition(predicate(equal(var("v"), integer(0)))).build())
				.addSymbol(new Nonterminal.Builder(B).build())
				.addSymbol(new Nonterminal.Builder(C).build()).build();
		
		RuntimeRule r2 = RuntimeRule.withHead(A).addSymbol(Terminal.from(Char.from('a'))).build();
		RuntimeRule r3 = RuntimeRule.withHead(B).addSymbol(Terminal.from(Char.from('b'))).build();
		
		RuntimeRule r4 = RuntimeRule.withHead(C).addSymbol(Terminal.from(Char.from('c'))).build();
		
		grammar = RuntimeGrammar.builder().addRules(r0, r1_1, r1_2, r2, r3, r4).build();
		
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("abc");
        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParserTree(input);

        assertNotNull(result);
	}

}
