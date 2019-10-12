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

package org.iguana.parser.layout;

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.RuntimeGrammar;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.RuntimeRule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

/**
 * 
 * S ::= A B
 * A ::= a
 * B ::= b
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest1 {

	private static RuntimeGrammar getGrammar() {
		Terminal a = Terminal.from(Char.from('a'));
		Terminal b = Terminal.from(Char.from('b'));
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		
		Nonterminal L = Nonterminal.builder("L").setNodeType(NonterminalNodeType.Layout).build();
		
		RuntimeRule r1 = RuntimeRule.withHead(S).addSymbols(A, B).setLayout(L).build();
		RuntimeRule r2 = RuntimeRule.withHead(A).addSymbol(a).setLayout(L).build();
		RuntimeRule r3 = RuntimeRule.withHead(B).addSymbol(b).setLayout(L).build();

		RuntimeRule layout = RuntimeRule.withHead(L).addSymbol(Terminal.from(Char.from(' '))).build();

		return new LayoutWeaver().transform(RuntimeGrammar.builder().addRules(r1, r2, r3, layout).build());
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a b");
		RuntimeGrammar grammar = getGrammar();

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParserTree(input);

        assertNotNull(result);
    }
}
