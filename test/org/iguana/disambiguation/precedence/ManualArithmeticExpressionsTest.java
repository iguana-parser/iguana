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

package org.iguana.disambiguation.precedence;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * E ::= E + T
 *     | T
 * 
 * T ::= T * F
 *     | F
 *     
 * F ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class ManualArithmeticExpressionsTest {

	private GLLParser parser;
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Nonterminal E = Nonterminal.withName("E");
		Nonterminal T = Nonterminal.withName("T");
		Nonterminal F = Nonterminal.withName("F");

		// E ::= E + T
		Rule rule1 = Rule.withHead(E).addSymbols(E, Character.from('+'), T).build();
		
		// E ::= T
		Rule rule2 = Rule.withHead(E).addSymbol(T).build();
		
		// T ::= T * F
		Rule rule3 = Rule.withHead(T).addSymbols(T, Character.from('*'), F).build();
		
		// T ::= F
		Rule rule4 = Rule.withHead(T).addSymbol(F).build();
		
		// F ::= a
		Rule rule5 = Rule.withHead(F).addSymbol(Character.from('a')).build();

		grammar = Grammar.builder().addRules(rule1, rule2, rule3, rule4, rule5).build();
	}

	@Test
	public void testParser() {
		Input input = Input.fromString("a*a+a");
		parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("E"));
		assertTrue(result.isParseSuccess());
		// TODO: add tree comparison text here.
	}
	
}
