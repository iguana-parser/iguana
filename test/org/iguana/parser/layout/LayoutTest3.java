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
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * 
 * S ::= A B+ C
 * A ::= a
 * B ::= b
 * C ::= c
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest3 {

    static Terminal a = Terminal.from(Char.from('a'));
    static Terminal b = Terminal.from(Char.from('b'));
    static Terminal c = Terminal.from(Char.from('c'));
    static Terminal space = Terminal.from(Char.from(' '));
    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Nonterminal B = Nonterminal.withName("B");
    static Nonterminal C = Nonterminal.withName("C");
    static Nonterminal L = Nonterminal.builder("L").setNodeType(NonterminalNodeType.Layout).build();

    static RuntimeRule r1 = RuntimeRule.withHead(S).addSymbols(A, Plus.from(B), C).setLayout(L).build();
    static RuntimeRule r2 = RuntimeRule.withHead(A).addSymbol(a).setLayout(L).build();
    static RuntimeRule r3 = RuntimeRule.withHead(B).addSymbol(b).setLayout(L).build();
    static RuntimeRule r4 = RuntimeRule.withHead(C).addSymbol(c).setLayout(L).build();

    static RuntimeRule layout = RuntimeRule.withHead(L).addSymbol(space).build();


    private static RuntimeGrammar getGrammar() {
        RuntimeGrammar grammar = RuntimeGrammar.builder().addRules(r1, r2, r3, r4, layout).build();
        return new LayoutWeaver().transform(new EBNFToBNF().transform(grammar));
    }

	@Test
	public void test() {
		Input input = Input.fromString("a b b b b c");
		RuntimeGrammar grammar = getGrammar();

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParserTree(input);

        assertNotNull(result);
	}
}
