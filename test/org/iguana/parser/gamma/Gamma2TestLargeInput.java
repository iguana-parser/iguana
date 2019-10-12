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

package org.iguana.parser.gamma;

import iguana.regex.Char;
import iguana.utils.input.Input;
import org.iguana.grammar.RuntimeGrammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.RuntimeRule;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.DesugarStartSymbol;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Gamma2TestLargeInput {

	private static Input getInput(int size) {
		return Input.fromString(Stream.generate(() -> "b").limit(size).collect(Collectors.joining()));
	}

	private static RuntimeGrammar getGrammar() {
		Nonterminal S = Nonterminal.withName("S");
		Terminal b = Terminal.from(Char.from('b'));
		RuntimeRule rule1 = RuntimeRule.withHead(S).addSymbols(S, S, S).build();
		RuntimeRule rule2 = RuntimeRule.withHead(S).addSymbols(S, S).build();
		RuntimeRule rule3 = RuntimeRule.withHead(S).addSymbols(b).build();
		return new DesugarStartSymbol().transform(RuntimeGrammar.builder().addRules(rule1, rule2, rule3).setStartSymbol(Start.from(S)).build());
	}

	private static Nonterminal getStartSymbol() {
		return Nonterminal.withName("S");
	}


//	static List<Function<GrammarGraph, ParseResult>> results = new ArrayList<>();
//
//	static {
//
//		// 50
//		results.add((GrammarGraph registry) -> new ParseSuccess(null,
//				ParseStatistics.builder()
//							   .setDescriptorsCount(6329)
//							   .setGSSNodesCount(51)
//							   .setGSSEdgesCount(3877)
//							   .setNonterminalNodesCount(1275)
//							   .setTerminalNodesCount(50)
//							   .setIntermediateNodesCount(1225)
//							   .setPackedNodesCount(61300)
//							   .setAmbiguousNodesCount(2352)
//							   .build()));
//
//		// 100
//		results.add((GrammarGraph registry) -> new ParseSuccess(null,
//				ParseStatistics.builder()
//							   .setDescriptorsCount(25154)
//							   .setGSSNodesCount(101)
//							   .setGSSEdgesCount(15252)
//							   .setNonterminalNodesCount(5050)
//							   .setTerminalNodesCount(100)
//							   .setIntermediateNodesCount(4950)
//							   .setPackedNodesCount(495100)
//							   .setAmbiguousNodesCount(9702)
//							   .build()));

		// 150
//		results.add((GrammarRegistry registry) -> new ParseSuccess(null, 
//				ParseStatistics.builder()
//							   .setDescriptorsCount(56479)
//							   .setGSSNodesCount(151)
//							   .setGSSEdgesCount(34127)
//							   .setNonterminalNodesCount(11325)
//							   .setTerminalNodesCount(150)
//							   .setIntermediateNodesCount(11175)
//							   .setPackedNodesCount(1676400) 
//							   .setAmbiguousNodesCount(22052)
//							   .build()));

		// 200
//		results.add((GrammarRegistry registry) -> new ParseSuccess(null, 
//				ParseStatistics.builder()
//							   .setDescriptorsCount(100304)
//							   .setGSSNodesCount(201)
//							   .setGSSEdgesCount(60502)
//							   .setNonterminalNodesCount(20100)
//							   .setTerminalNodesCount(200)
//							   .setIntermediateNodesCount(19900)
//							   .setPackedNodesCount(3980200) 
//							   .setAmbiguousNodesCount(39402) 
//							   .build()));

}
