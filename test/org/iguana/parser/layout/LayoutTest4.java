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

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Plus;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Test;

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

	private static Grammar getGrammar() {

		Character a = Character.from('a');
		Character b = Character.from('b');
		Character c = Character.from('c');
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		Nonterminal L = Nonterminal.withName("L");
		
		Rule r1 = Rule.withHead(S).addSymbols(A, Plus.builder(B).addSeparator(Character.from(',')).build(), C).setLayout(L).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).setLayout(L).build();
		Rule r3 = Rule.withHead(B).addSymbol(b).setLayout(L).build();
		Rule r4 = Rule.withHead(C).addSymbol(c).setLayout(L).build();
		
		Rule layout = Rule.withHead(L).addSymbol(Character.from(' ')).build();
		
		return Grammar.builder().addRules(r1, r2, r3, r4, layout).build();
	}
	
	@Test
	public void test() {
		EBNFToBNF ebnfToBNF = new EBNFToBNF();
		Grammar bnfGrammar = ebnfToBNF.transform(getGrammar());
		System.out.println(bnfGrammar);
		
		Input input = Input.fromString("a b , b , b , b c");
		Grammar grammar = getGrammar();
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
	}
}
