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

package org.iguana.parser.basic;

import iguana.regex.Character;
import iguana.regex.CharacterRange;
import iguana.regex.EOF;
import iguana.regex.Epsilon;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.ParseStatistics;
import org.junit.Before;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * 	E  ::= T E1
 * 	E1 ::= + T E1 | epsilon
 *  T  ::= F T1
 *  T1 ::= * F T1 |  epsilon
 *  F  ::= (E) | a
 *  
 */
public class Test20 {

	private Grammar grammar;
	
	private Nonterminal E = Nonterminal.withName("E");
	private Nonterminal T = Nonterminal.withName("T");
	private Nonterminal E1 = Nonterminal.withName("E1");
	private Nonterminal F = Nonterminal.withName("F");
	private Nonterminal T1 = Nonterminal.withName("T1");
	private Terminal plus = Terminal.from(Character.from('+'));
	private Terminal star = Terminal.from(Character.from('*'));
	private Terminal a = Terminal.from(Character.from('a'));
	private Terminal openPar = Terminal.from(Character.from('('));
	private Terminal closePar = Terminal.from(Character.from(')'));

    Rule r1 = Rule.withHead(E).addSymbols(T, E1).build();
    Rule r2 = Rule.withHead(E1).addSymbols(plus, T, E1).build();
    Rule r3 = Rule.withHead(E1).build();
    Rule r4 = Rule.withHead(T).addSymbols(F, T1).build();
    Rule r5 = Rule.withHead(T1).addSymbols(star, F, T1).build();
    Rule r6 = Rule.withHead(T1).build();
    Rule r7 = Rule.withHead(F).addSymbols(openPar, E, closePar).build();
    Rule r8 = Rule.withHead(F).addSymbol(a).build();

	@Before
	public void createGrammar() {
		Grammar.Builder builder = new Grammar.Builder();
		grammar = builder.addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).addRule(r7).addRule(r8).build();
	}
	
	@Test
	public void testFirstSets() {
        FirstFollowSets ff = new FirstFollowSets(grammar);
		assertEquals(set(CharacterRange.from('('), CharacterRange.from('a')), ff.getFirstSet(E));
		assertEquals(set(CharacterRange.from('+'), Epsilon.asCharacterRange()), ff.getFirstSet(E1));
		assertEquals(set(CharacterRange.from('*'), Epsilon.asCharacterRange()), ff.getFirstSet(T1));
		assertEquals(set(CharacterRange.from('('), CharacterRange.from('a')), ff.getFirstSet(T));
		assertEquals(set(CharacterRange.from('('), CharacterRange.from('a')), ff.getFirstSet(F));
	}

    @Test
	public void testFollowSets() {
        FirstFollowSets ff = new FirstFollowSets(grammar);
		assertEquals(set(CharacterRange.from(')'), EOF.asCharacterRange()), ff.getFollowSet(E));
		assertEquals(set(CharacterRange.from(')'), EOF.asCharacterRange()), ff.getFollowSet(E1));
		assertEquals(set(CharacterRange.from('+'), CharacterRange.from(')'), EOF.asCharacterRange()), ff.getFollowSet(T1));
		assertEquals(set(CharacterRange.from('+'), CharacterRange.from(')'), EOF.asCharacterRange()), ff.getFollowSet(T));
		assertEquals(set(CharacterRange.from('+'), CharacterRange.from('*'), CharacterRange.from(')'), EOF.asCharacterRange()), ff.getFollowSet(F));
	}
	
	@Test
	public void testParser() {
        Input input = Input.fromString("a+a*a+a");
        GrammarGraph graph = GrammarGraph.from(grammar, input);
		ParseResult result = Iguana.parse(input, graph, E);
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult(graph, input), result);
	}

    private ParseSuccess getParseResult(GrammarGraph graph, Input input) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(29)
                .setGSSNodesCount(15)
                .setGSSEdgesCount(14)
                .setNonterminalNodesCount(15)
                .setTerminalNodesCount(10)
                .setIntermediateNodesCount(10)
                .setPackedNodesCount(25)
                .setAmbiguousNodesCount(0).build();
        return new ParseSuccess(getSPPF(new SPPFNodeFactory(graph), input), statistics, input);
    }
	
	private NonterminalNode getSPPF(SPPFNodeFactory factory, Input input) {
        TerminalNode node0 = factory.createTerminalNode("a", 0, 1, input);
        NonterminalNode node1 = factory.createNonterminalNode("F", "F ::= a .", node0, input);
        TerminalNode node2 = factory.createTerminalNode("epsilon", 1, 1, input);
        NonterminalNode node3 = factory.createNonterminalNode("T1", "T1 ::= .", node2, input);
        IntermediateNode node4 = factory.createIntermediateNode("T ::= F T1 .", node1, node3);
        NonterminalNode node5 = factory.createNonterminalNode("T", "T ::= F T1 .", node4, input);
        TerminalNode node6 = factory.createTerminalNode("+", 1, 2, input);
        TerminalNode node7 = factory.createTerminalNode("a", 2, 3, input);
        NonterminalNode node8 = factory.createNonterminalNode("F", "F ::= a .", node7, input);
        TerminalNode node9 = factory.createTerminalNode("*", 3, 4, input);
        TerminalNode node10 = factory.createTerminalNode("a", 4, 5, input);
        NonterminalNode node11 = factory.createNonterminalNode("F", "F ::= a .", node10, input);
        IntermediateNode node12 = factory.createIntermediateNode("T1 ::= * F . T1", node9, node11);
        TerminalNode node13 = factory.createTerminalNode("epsilon", 5, 5, input);
        NonterminalNode node14 = factory.createNonterminalNode("T1", "T1 ::= .", node13, input);
        IntermediateNode node15 = factory.createIntermediateNode("T1 ::= * F T1 .", node12, node14);
        NonterminalNode node16 = factory.createNonterminalNode("T1", "T1 ::= * F T1 .", node15, input);
        IntermediateNode node17 = factory.createIntermediateNode("T ::= F T1 .", node8, node16);
        NonterminalNode node18 = factory.createNonterminalNode("T", "T ::= F T1 .", node17, input);
        IntermediateNode node19 = factory.createIntermediateNode("E1 ::= + T . E1", node6, node18);
        TerminalNode node20 = factory.createTerminalNode("+", 5, 6, input);
        TerminalNode node21 = factory.createTerminalNode("a", 6, 7, input);
        NonterminalNode node22 = factory.createNonterminalNode("F", "F ::= a .", node21, input);
        TerminalNode node23 = factory.createTerminalNode("epsilon", 7, 7, input);
        NonterminalNode node24 = factory.createNonterminalNode("T1", "T1 ::= .", node23, input);
        IntermediateNode node25 = factory.createIntermediateNode("T ::= F T1 .", node22, node24);
        NonterminalNode node26 = factory.createNonterminalNode("T", "T ::= F T1 .", node25, input);
        IntermediateNode node27 = factory.createIntermediateNode("E1 ::= + T . E1", node20, node26);
        NonterminalNode node28 = factory.createNonterminalNode("E1", "E1 ::= .", node23, input);
        IntermediateNode node29 = factory.createIntermediateNode("E1 ::= + T E1 .", node27, node28);
        NonterminalNode node30 = factory.createNonterminalNode("E1", "E1 ::= + T E1 .", node29, input);
        IntermediateNode node31 = factory.createIntermediateNode("E1 ::= + T E1 .", node19, node30);
        NonterminalNode node32 = factory.createNonterminalNode("E1", "E1 ::= + T E1 .", node31, input);
        IntermediateNode node33 = factory.createIntermediateNode("E ::= T E1 .", node5, node32);
        NonterminalNode node34 = factory.createNonterminalNode("E", "E ::= T E1 .", node33, input);
        return node34;
    }

}
