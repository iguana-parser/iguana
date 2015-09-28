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

import static org.junit.Assert.assertTrue;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * 
 * S ::= a S b S
 *     | a S !>> b
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar3 {

	Nonterminal S = Nonterminal.withName("S");
	Character s = Character.from('s');
	Character a = Character.from('a');
	Character b = Character.from('b');
	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = Rule.withHead(S).addSymbols(a, Nonterminal.builder("S").addPreCondition(RegularExpressionCondition.notFollow(Character.from('b'))).build()).build();
		builder.addRule(rule1);
		
		Rule rule2 = Rule.withHead(S).addSymbols(a, S, b, S).build();
		builder.addRule(rule2);
		
		Rule rule3 = Rule.withHead(S).addSymbols(s).build();
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF(parser.getGrammarGraph())));
	}
	
//	private SPPFNode getExpectedSPPF(GrammarGraph registry) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
//		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5);
//		PackedNode node2 = factory.createPackedNode("S ::= a S b S .", 4, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("S ::= a S b . S", 0, 4);
//		PackedNode node4 = factory.createPackedNode("S ::= a S b . S", 3, node3);
//		IntermediateNode node5 = factory.createIntermediateNode("S ::= a S . b S", 0, 3);
//		PackedNode node6 = factory.createPackedNode("S ::= a S . b S", 1, node5);
//		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
//		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 3);
//		PackedNode node9 = factory.createPackedNode("S ::= a S .", 2, node8);
//		TerminalNode node10 = factory.createTerminalNode("a", 1, 2);
//		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 3);
//		PackedNode node12 = factory.createPackedNode("S ::= s .", 3, node11);
//		TerminalNode node13 = factory.createTerminalNode("s", 2, 3);
//		node12.addChild(node13);
//		node11.addChild(node12);
//		node9.addChild(node10);
//		node9.addChild(node11);
//		node8.addChild(node9);
//		node6.addChild(node7);
//		node6.addChild(node8);
//		node5.addChild(node6);
//		TerminalNode node14 = factory.createTerminalNode("b", 3, 4);
//		node4.addChild(node5);
//		node4.addChild(node14);
//		node3.addChild(node4);
//		NonterminalNode node15 = factory.createNonterminalNode("S", 0, 4, 5);
//		PackedNode node16 = factory.createPackedNode("S ::= s .", 5, node15);
//		TerminalNode node17 = factory.createTerminalNode("s", 4, 5);
//		node16.addChild(node17);
//		node15.addChild(node16);
//		node2.addChild(node3);
//		node2.addChild(node15);
//		PackedNode node18 = factory.createPackedNode("S ::= a S .", 1, node1);
//		NonterminalNode node20 = factory.createNonterminalNode("S", 0, 1, 5);
//		PackedNode node21 = factory.createPackedNode("S ::= a S b S .", 4, node20);
//		IntermediateNode node22 = factory.createIntermediateNode("S ::= a S b . S", 1, 4);
//		PackedNode node23 = factory.createPackedNode("S ::= a S b . S", 3, node22);
//		IntermediateNode node24 = factory.createIntermediateNode("S ::= a S . b S", 1, 3);
//		PackedNode node25 = factory.createPackedNode("S ::= a S . b S", 2, node24);
//		node25.addChild(node10);
//		node25.addChild(node11);
//		node24.addChild(node25);
//		node23.addChild(node24);
//		node23.addChild(node14);
//		node22.addChild(node23);
//		node21.addChild(node22);
//		node21.addChild(node15);
//		node20.addChild(node21);
//		node18.addChild(node7);
//		node18.addChild(node20);
//		node1.addChild(node2);
//		node1.addChild(node18);
//		return node1;
//	}

}
