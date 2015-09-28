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
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

/**
 * S ::= "if" L S L "then" L S 
 *     | s
 *     
 * L ::= " "    
 * 
 * @author Ali Afroozeh
 *
 */

public class KeywordTest3 {
	
	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Terminal iff = Terminal.from(Sequence.from("if"));
	private Terminal then = Terminal.from(Sequence.from("then"));
	private Nonterminal L = Nonterminal.withName("L");
	private Character s = Character.from('s');
	private Character ws = Character.from(' ');

	@Before
	public void init() {
		Rule r1 = Rule.withHead(S).addSymbols(iff, L, S, L, then, L, S).build();
		Rule r2 = Rule.withHead(S).addSymbol(s).build();
		Rule r3 = Rule.withHead(L).addSymbol(ws).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	
//	@Test
//	public void testFirstSet() {
//		assertEquals(set(iff, s), grammar.getFirstSet(S));
//	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("if s then s");
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
//		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF(parser.getGrammarGraph())));
	}
		
//	private SPPFNode getSPPF(GrammarGraph registry) {
//		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
//		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 11);
//		PackedNode node2 = factory.createPackedNode("S ::= (i f) L S L (t h e n) L S .", 10, node1);
//		IntermediateNode node3 = factory.createIntermediateNode("S ::= (i f) L S L (t h e n) L . S", 0, 10);
//		PackedNode node4 = factory.createPackedNode("S ::= (i f) L S L (t h e n) L . S", 9, node3);
//		IntermediateNode node5 = factory.createIntermediateNode("S ::= (i f) L S L (t h e n) . L S", 0, 9);
//		PackedNode node6 = factory.createPackedNode("S ::= (i f) L S L t h e n . L S", 5, node5);
//		IntermediateNode node7 = factory.createIntermediateNode("S ::= (i f) L S L . (t h e n) L S", 0, 5);
//		PackedNode node8 = factory.createPackedNode("S ::= (i f) L S L . (t h e n) L S", 4, node7);
//		IntermediateNode node9 = factory.createIntermediateNode("S ::= (i f) L S . L (t h e n) L S", 0, 4);
//		PackedNode node10 = factory.createPackedNode("S ::= (i f) L S . L (t h e n) L S", 3, node9);
//		IntermediateNode node11 = factory.createIntermediateNode("S ::= (i f) L . S L (t h e n) L S", 0, 3);
//		PackedNode node12 = factory.createPackedNode("S ::= (i f) L . S L (t h e n) L S", 2, node11);
//		TerminalNode node13 = factory.createTerminalNode("(i f)", 0, 2);
//		NonterminalNode node14 = factory.createNonterminalNode("L", 0, 2, 3);
//		PackedNode node15 = factory.createPackedNode("L ::= \\u0020 .", 3, node14);
//		TerminalNode node16 = factory.createTerminalNode("\\u0020", 2, 3);
//		node15.addChild(node16);
//		node14.addChild(node15);
//		node12.addChild(node13);
//		node12.addChild(node14);
//		node11.addChild(node12);
//		NonterminalNode node17 = factory.createNonterminalNode("S", 0, 3, 4);
//		PackedNode node18 = factory.createPackedNode("S ::= s .", 4, node17);
//		TerminalNode node19 = factory.createTerminalNode("s", 3, 4);
//		node18.addChild(node19);
//		node17.addChild(node18);
//		node10.addChild(node11);
//		node10.addChild(node17);
//		node9.addChild(node10);
//		NonterminalNode node20 = factory.createNonterminalNode("L", 0, 4, 5);
//		PackedNode node21 = factory.createPackedNode("L ::= \\u0020 .", 5, node20);
//		TerminalNode node22 = factory.createTerminalNode("\\u0020", 4, 5);
//		node21.addChild(node22);
//		node20.addChild(node21);
//		node8.addChild(node9);
//		node8.addChild(node20);
//		node7.addChild(node8);
//		TerminalNode node23 = factory.createTerminalNode("(t h e n)", 5, 9);
//		node6.addChild(node7);
//		node6.addChild(node23);
//		node5.addChild(node6);
//		NonterminalNode node24 = factory.createNonterminalNode("L", 0, 9, 10);
//		PackedNode node25 = factory.createPackedNode("L ::= \\u0020 .", 10, node24);
//		TerminalNode node26 = factory.createTerminalNode("\\u0020", 9, 10);
//		node25.addChild(node26);
//		node24.addChild(node25);
//		node4.addChild(node5);
//		node4.addChild(node24);
//		node3.addChild(node4);
//		NonterminalNode node27 = factory.createNonterminalNode("S", 0, 10, 11);
//		PackedNode node28 = factory.createPackedNode("S ::= s .", 11, node27);
//		TerminalNode node29 = factory.createTerminalNode("s", 10, 11);
//		node28.addChild(node29);
//		node27.addChild(node28);
//		node2.addChild(node3);
//		node2.addChild(node27);
//		node1.addChild(node2);
//		return node1;
//	}
	
}
