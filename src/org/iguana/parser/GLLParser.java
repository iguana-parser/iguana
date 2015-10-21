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

package org.iguana.parser;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.util.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public interface GLLParser {
	
	ParseResult parse(Input input, GrammarGraph grammarGraph, Configuration config, Nonterminal startSymbol, Map<String, ?> map, boolean global);
	
	default ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal startSymbol) {
		return parse(input, grammarGraph, Configuration.DEFAULT, startSymbol, Collections.emptyMap(), true);
	}
	
	default ParseResult parse(Input input, Grammar grammar, Configuration config, Nonterminal startSymbol) {
		return parse(input, GrammarGraph.from(grammar, input, config), startSymbol);
	}
	
	default ParseResult parse(Input input, Grammar grammar, Configuration config, Nonterminal startSymbol, Map<String, ?> map) {
		return parse(input, GrammarGraph.from(grammar, input, config), config, startSymbol, map, true);
	}
}
