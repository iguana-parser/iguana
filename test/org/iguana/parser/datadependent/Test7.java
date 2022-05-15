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

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * S ::= E(0,0)
 * 
 * E(l,r) ::= [4 >= l, 4 >= r] E(5,0) '^' E(l,4) // right
 *          | [3 >= l, 3 >= r] E(3,0) '+' E(l,4) // left
 *          | [2 >= l] '-' E(0,0)
 *          | a
 *
 */

public class Test7 {
	
	private RuntimeGrammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = new Nonterminal.Builder("E").addParameters("l", "r").build();
		
		Terminal hat = Terminal.from(Char.from('^'));
        Terminal plus = Terminal.from(Char.from('+'));
		
		RuntimeRule r0 = RuntimeRule.withHead(S).addSymbol(new Nonterminal.Builder(E).apply(integer(0), integer(0)).build()).build();
		
		RuntimeRule r1_1 = RuntimeRule.withHead(E)
					.addSymbol(new Nonterminal.Builder(E).apply(integer(5), integer(0))
							.addPreCondition(predicate(greaterEq(integer(4), var("l"))))
							.addPreCondition(predicate(greaterEq(integer(4), var("r")))).build())
					.addSymbol(hat)
					.addSymbol(new Nonterminal.Builder(E).apply(var("l"), integer(4)).build()).build();
		
		RuntimeRule r1_2 = RuntimeRule.withHead(E)
				.addSymbol(new Nonterminal.Builder(E).apply(integer(3), integer(0))
						.addPreCondition(predicate(greaterEq(integer(3), var("l"))))
						.addPreCondition(predicate(greaterEq(integer(3), var("r")))).build())
				.addSymbol(plus)
				.addSymbol(new Nonterminal.Builder(E).apply(var("l"), integer(4)).build()).build();
		
		RuntimeRule r1_3 = RuntimeRule.withHead(E)
				.addSymbol(new Terminal.Builder(Char.from('-'))
					.addPreCondition(predicate(greaterEq(integer(2), var("l")))).build())
				.addSymbol(new Nonterminal.Builder(E).apply(integer(0), integer(0)).build()).build();
		
		RuntimeRule r1_4 = RuntimeRule.withHead(E).addSymbol(Terminal.from(Char.from('a'))).build();
		
		grammar = RuntimeGrammar.builder().addRules(r0, r1_1, r1_2, r1_3, r1_4).build();
		
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a+a^a^-a+a");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
	}

}
