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

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ContextFreeCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Sequence;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;

/**
 * 
 * S ::= a S b S 
 * 	   | a S \ a S b S 
 *     | s
 * 
 * @author Ali Afroozeh
 * 
 */
// TODO: context-free conditions don't work
public class DanglingElseGrammar2 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Character s = Character.from('s');
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Sequence<?> group = Sequence.builder(a, S).addPreCondition(ContextFreeCondition.notMatch(a, S, b, S)).build();

	@Before
	public void createGrammar() {

		Grammar.Builder builder = new Grammar.Builder();

		Rule rule1 = Rule.withHead(S).addSymbols(group).build();
		builder.addRule(rule1);
		
		Rule rule2 = Rule.withHead(S).addSymbols(a, S, b, S).build();
		builder.addRule(rule2);

		Rule rule3 = Rule.withHead(S).addSymbols(s).build();
		builder.addRule(rule3);
		
		grammar = builder.build();
	}

	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getGrammarGraph())));
	}

	private SPPFNode getExpectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5);
		PackedNode node2 = factory.createPackedNode("S ::= (a S) .", 0, node1);
		NonterminalNode node3 = factory.createNonterminalNode("(a S)", 0, 0, 5);
		PackedNode node4 = factory.createPackedNode("(a S) ::= a S .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 5);
		PackedNode node7 = factory.createPackedNode("S ::= a S b S .", 4, node6);
		IntermediateNode node8 = factory.createIntermediateNode("S ::= a S b . S", 1, 4);
		PackedNode node9 = factory.createPackedNode("S ::= a S b . S", 3, node8);
		IntermediateNode node10 = factory.createIntermediateNode("S ::= a S . b S", 1, 3);
		PackedNode node11 = factory.createPackedNode("S ::= a S . b S", 2, node10);
		TerminalNode node12 = factory.createTerminalNode("a", 1, 1);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3);
		PackedNode node14 = factory.createPackedNode("S ::= s .", 2, node13);
		TerminalNode node15 = factory.createTerminalNode("s", 2, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		node11.addChild(node12);
		node11.addChild(node13);
		node10.addChild(node11);
		TerminalNode node16 = factory.createTerminalNode("b", 3, 1);
		node9.addChild(node10);
		node9.addChild(node16);
		node8.addChild(node9);
		NonterminalNode node17 = factory.createNonterminalNode("S", 0, 4, 5);
		PackedNode node18 = factory.createPackedNode("S ::= s .", 4, node17);
		TerminalNode node19 = factory.createTerminalNode("s", 4, 1);
		node18.addChild(node19);
		node17.addChild(node18);
		node7.addChild(node8);
		node7.addChild(node17);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		node2.addChild(node3);
		PackedNode node20 = factory.createPackedNode("S ::= a S b S .", 4, node1);
		IntermediateNode node21 = factory.createIntermediateNode("S ::= a S b . S", 0, 4);
		PackedNode node22 = factory.createPackedNode("S ::= a S b . S", 3, node21);
		IntermediateNode node23 = factory.createIntermediateNode("S ::= a S . b S", 0, 3);
		PackedNode node24 = factory.createPackedNode("S ::= a S . b S", 1, node23);
		NonterminalNode node25 = factory.createNonterminalNode("S", 0, 1, 3);
		PackedNode node26 = factory.createPackedNode("S ::= (a S) .", 1, node25);
		NonterminalNode node27 = factory.createNonterminalNode("(a S)", 0, 1, 3);
		PackedNode node28 = factory.createPackedNode("(a S) ::= a S .", 2, node27);
		node28.addChild(node12);
		node28.addChild(node13);
		node27.addChild(node28);
		node26.addChild(node27);
		node25.addChild(node26);
		node24.addChild(node5);
		node24.addChild(node25);
		node23.addChild(node24);
		node22.addChild(node23);
		node22.addChild(node16);
		node21.addChild(node22);
		node20.addChild(node21);
		node20.addChild(node17);
		node1.addChild(node2);
		node1.addChild(node20);
		return node1;
	}

}
