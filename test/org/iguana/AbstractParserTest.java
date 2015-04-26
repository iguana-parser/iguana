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

package org.iguana;

import static org.junit.Assert.*;

import java.util.function.Function;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.util.Input;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class AbstractParserTest {
	
	@Parameter(value = 0)
	public Input input;
	
	@Parameter(value = 1)
	public Grammar grammar;
	
	@Parameter(value = 2)
	public Nonterminal startSymbol;
	
	@Parameter(value = 3)
	public GLLParser parser;

	@Parameter(value = 4)
	public Function<GrammarGraph, ParseResult> expectedResult;
	
	@Test
	public void testParser() {
				
		ParseResult result = parser.parse(input, grammar, startSymbol);
		assertTrue(result.isParseSuccess());
		
		// Checking the parse tree if it exists
		ParseResult expected = expectedResult.apply(parser.getGrammarGraph());
		
		if (expected.asParseSuccess().getRoot() == null) {
			assertEquals(expected.asParseSuccess().getStatistics(), result.asParseSuccess().getStatistics());
		} else {
			assertEquals(expected, result);
		}			
	}
	
}
