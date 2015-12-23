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

import static iguana.utils.collections.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import iguana.parsetrees.slot.NonterminalNodeType;
import iguana.parsetrees.term.Term;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.LayoutWeaver;
import iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Test;

import static iguana.parsetrees.term.TermFactory.*;

import iguana.utils.input.Input;

/**
 * 
 * S ::= A {B ,}+ C
 * A ::= a
 * B ::= b
 * C ::= c
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest4 {

    static Terminal a = Terminal.from(Character.from('a'));
    static Terminal b = Terminal.from(Character.from('b'));
    static Terminal c = Terminal.from(Character.from('c'));
    static Nonterminal S = Nonterminal.withName("S");
    static Nonterminal A = Nonterminal.withName("A");
    static Nonterminal B = Nonterminal.withName("B");
    static Nonterminal C = Nonterminal.withName("C");
    static Terminal space = Terminal.from(Character.from(' '));
    static Terminal comma = Terminal.from(Character.from(','));

    static Nonterminal L = Nonterminal.builder("L").setType(NonterminalNodeType.Layout()).build();

    static Rule r1 = Rule.withHead(S).addSymbols(A, Plus.builder(B).addSeparator(comma).build(), C).setLayout(L).build();
    static Rule r2 = Rule.withHead(A).addSymbol(a).setLayout(L).build();
    static Rule r3 = Rule.withHead(B).addSymbol(b).setLayout(L).build();
    static Rule r4 = Rule.withHead(C).addSymbol(c).setLayout(L).build();

    static Rule layout = Rule.withHead(L).addSymbol(space).build();

	private static Grammar getGrammar() {
        Grammar grammar = Grammar.builder().addRules(r1, r2, r3, r4, layout).build();
        return new LayoutWeaver().transform(new EBNFToBNF().transform(grammar));
    }
	
	@Test
	public void test() {
		Input input = Input.fromString("a b , b , b , b c");
		Grammar grammar = getGrammar();
		ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
        assertTrue(getTree(input).equals(result.asParseSuccess().getTree()));
	}

    private Term getTree(Input input) {
        Term t0 = createTerminalTerm(a, 0, 1, input);
        Term t1 = createNonterminalTerm(r2, list(t0), input);
        Term t2 = createTerminalTerm(space, 1, 2, input);
        Term t3 = createNonterminalTerm(layout, list(t2), input);
        Term t4 = createTerminalTerm(b, 2, 3, input);
        Term t5 = createNonterminalTerm(r3, list(t4), input);
        Term t6 = createTerminalTerm(space, 3, 4, input);
        Term t7 = createNonterminalTerm(layout, list(t6), input);
        Term t8 = createTerminalTerm(comma, 4, 5, input);
        Term t9 = createTerminalTerm(space, 5, 6, input);
        Term t10 = createNonterminalTerm(layout, list(t9), input);
        Term t11 = createTerminalTerm(b, 6, 7, input);
        Term t12 = createNonterminalTerm(r3, list(t11), input);
        Term t13 = createTerminalTerm(space, 7, 8, input);
        Term t14 = createNonterminalTerm(layout, list(t13), input);
        Term t15 = createTerminalTerm(comma, 8, 9, input);
        Term t16 = createTerminalTerm(space, 9, 10, input);
        Term t17 = createNonterminalTerm(layout, list(t16), input);
        Term t18 = createTerminalTerm(b, 10, 11, input);
        Term t19 = createNonterminalTerm(r3, list(t18), input);
        Term t20 = createTerminalTerm(space, 11, 12, input);
        Term t21 = createNonterminalTerm(layout, list(t20), input);
        Term t22 = createTerminalTerm(comma, 12, 13, input);
        Term t23 = createTerminalTerm(space, 13, 14, input);
        Term t24 = createNonterminalTerm(layout, list(t23), input);
        Term t25 = createTerminalTerm(b, 14, 15, input);
        Term t26 = createNonterminalTerm(r3, list(t25), input);
        Term t27 = createPlus(list(t5, t7, t8, t10, t12, t14, t15, t17, t19, t21, t22, t24, t26));
        Term t28 = createTerminalTerm(space, 15, 16, input);
        Term t29 = createNonterminalTerm(layout , list(t28), input);
        Term t30 = createTerminalTerm(c, 16, 17, input);
        Term t31 = createNonterminalTerm(r4, list(t30), input);
        Term t32 = createNonterminalTerm(r1, list(t1, t3, t27, t29, t31), input);
        return t32;
    }
}
