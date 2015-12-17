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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import iguana.parsetrees.slot.NonterminalNodeType;
import iguana.parsetrees.sppf.*;
import iguana.parsetrees.term.Term;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.ParseSuccess;
import iguana.regex.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.ParseStatistics;
import org.junit.Before;
import org.junit.Test;

import static iguana.parsetrees.sppf.SPPFNodeFactory.*;
import static iguana.parsetrees.term.TermFactory.*;

import static iguana.utils.collections.CollectionsUtil.*;

import iguana.utils.input.Input;

/**
 * 
 * S ::= A B C
 * A ::= a
 * B ::= epsilon
 * C ::= c
 * 
 * L ::= ' '
 *     | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class LayoutTest2 {

    Terminal a = Terminal.from(Character.from('a'));
    Terminal c = Terminal.from(Character.from('c'));
    Nonterminal S = Nonterminal.withName("S");
    Nonterminal A = Nonterminal.withName("A");
    Nonterminal B = Nonterminal.withName("B");
    Nonterminal C = Nonterminal.withName("C");

    Nonterminal L = Nonterminal.builder("L").setType(NonterminalNodeType.Layout()).build();

    Rule r1 = Rule.withHead(S).addSymbols(A, B, C).setLayout(L).build();
    Rule r2 = Rule.withHead(A).addSymbol(a).setLayout(L).build();
    Rule r3 = Rule.withHead(B).setLayout(L).build();
    Rule r4 = Rule.withHead(C).setLayout(L).addSymbol(c).build();

    Terminal space = Terminal.from(Character.from(' '));
    Rule layout1 = Rule.withHead(L).addSymbol(space).build();
    Rule layout2 = Rule.withHead(L).build();

    Grammar grammar;

    @Before
	public void init() {
        grammar = Grammar.builder().addRules(r1, r2, r3, r4, layout1, layout2).build();
        grammar = new LayoutWeaver().transform(grammar);
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a c");
        GrammarGraph graph = GrammarGraph.from(grammar, input);
        ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
        assertEquals(getParseResult(graph, input), result);
        assertTrue(getTree(input).equals(result.asParseSuccess().getTree()));
    }

    public ParseSuccess getParseResult(GrammarGraph graph, Input input) {
        ParseStatistics statistics = ParseStatistics.builder()
                .setDescriptorsCount(15)
                .setGSSNodesCount(7)
                .setGSSEdgesCount(7)
                .setNonterminalNodesCount(8)
                .setTerminalNodesCount(5)
                .setIntermediateNodesCount(6)
                .setPackedNodesCount(15)
                .setAmbiguousNodesCount(1).build();
        return new ParseSuccess(getSPPFNode(graph, input), statistics, input);
    }

    private Term getTree(Input input) {
        Term t0 = createTerminal(a, 0, 1, input);
        Term t1 = createRule(r2, list(t0), input);
        Term t2 = createEpsilon(1);
        Term t3 = createRule(layout1, list(t2), input);
        Term t4 = createEpsilon(1);
        Term t5 = createRule(r3, list(t4), input);
        Term t6 = createTerminal(space, 1, 2, input);
        Term t7 = createRule(layout2, list(t6), input);
        Term t8 = createEpsilon(2);
        Term t9 = createRule(r3, list(t8), input);
        Term t10 = createEpsilon(2);
        Term t11 = createRule(layout2, list(t10), input);
        Term t12 = createAmbiguity(list(list(t1, t3, t5, t7), list(t1, t7, t9, t11)));
        Term t13 = createTerminal(c, 2, 3, input);
        Term t14 = createRule(r4, list(t13), input);
        Term t15 = createRule(r1, list(t12, t14), input);
        return t15;
    }

    private NonterminalNode getSPPFNode(GrammarGraph registry, Input input) {
        TerminalNode node0 = createTerminalNode(registry.getSlot("a"), 0, 1, input);
        NonterminalNode node1 = createNonterminalNode(registry.getSlot("A"), registry.getSlot("A ::= a ."), node0, input);
        TerminalNode node2 = createTerminalNode(registry.getSlot("epsilon"), 1, 1, input);
        NonterminalNode node3 = createNonterminalNode(registry.getSlot("L"), registry.getSlot("L ::= ."), node2, input);
        IntermediateNode node4 = createIntermediateNode(registry.getSlot("S ::= A L . B L C"), node1, node3);
        NonterminalNode node5 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= ."), node2, input);
        IntermediateNode node6 = createIntermediateNode(registry.getSlot("S ::= A L B . L C"), node4, node5);
        TerminalNode node7 = createTerminalNode(registry.getSlot("\\u0020"), 1, 2, input);
        NonterminalNode node8 = createNonterminalNode(registry.getSlot("L"), registry.getSlot("L ::= \\u0020 ."), node7, input);
        IntermediateNode node9 = createIntermediateNode(registry.getSlot("S ::= A L . B L C"), node1, node8);
        TerminalNode node10 = createTerminalNode(registry.getSlot("epsilon"), 2, 2, input);
        NonterminalNode node11 = createNonterminalNode(registry.getSlot("B"), registry.getSlot("B ::= ."), node10, input);
        IntermediateNode node12 = createIntermediateNode(registry.getSlot("S ::= A L B . L C"), node9, node11);
        NonterminalNode node13 = createNonterminalNode(registry.getSlot("L"), registry.getSlot("L ::= ."), node10, input);
        IntermediateNode node14 = createIntermediateNode(registry.getSlot("S ::= A L B L . C"), node6, node8);
        node14.addPackedNode(registry.getSlot("S ::= A L B L . C"), node12, node13);
        TerminalNode node15 = createTerminalNode(registry.getSlot("c"), 2, 3, input);
        NonterminalNode node16 = createNonterminalNode(registry.getSlot("C"), registry.getSlot("C ::= c ."), node15, input);
        IntermediateNode node17 = createIntermediateNode(registry.getSlot("S ::= A L B L C ."), node14, node16);
        NonterminalNode node18 = createNonterminalNode(registry.getSlot("S"), registry.getSlot("S ::= A L B L C ."), node17, input);
        return node18;
    }
}
