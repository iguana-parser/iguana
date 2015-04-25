/*
 * Copyright (c) 2015, CWI
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

package org.iguana.parser.ambiguous;

import static org.iguana.util.Configurations.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.iguana.AbstractParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.ParseStatistics;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 *  S ::= S S S 
 *      | S S 
 *      | b
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Gamma2Test extends AbstractParserTest {

	@Parameters
    public static Collection<Object[]> data() {
		 List<Object[]> parameters = newConfigs.stream().map(c -> new Object[] {
	    		getInput1(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getNewParseResult1
	    	}).collect(Collectors.toList());
		 parameters.addAll(originalConfigs.stream().map(c -> new Object[] {
	    		getInput1(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getOriginalParseResult1
	    	}).collect(Collectors.toList()));
		 parameters.addAll(newConfigs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput2(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getNewParseResult2
	    	}).collect(Collectors.toList()));
		 parameters.addAll(originalConfigs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput2(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getOriginalParseResult2
	    	}).collect(Collectors.toList()));
		 parameters.addAll(newConfigs.stream().map(c -> new Object[] {
		    		getInput3(), 
		    		getGrammar(), 
		    		getStartSymbol(),
		    		ParserFactory.getParser(c, getInput3(), getGrammar()),
		    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getNewParseResult3
		    	}).collect(Collectors.toList()));
			 parameters.addAll(originalConfigs.stream().map(c -> new Object[] {
		    		getInput3(), 
		    		getGrammar(), 
		    		getStartSymbol(),
		    		ParserFactory.getParser(c, getInput3(), getGrammar()),
		    		(Function<GrammarGraph, ParseResult>) Gamma2Test::getOriginalParseResult3
		    	}).collect(Collectors.toList()));
		 return parameters;
    }
	
	private static Grammar getGrammar() {
		Nonterminal S = Nonterminal.withName("S");
		Character b = Character.from('b');
		Rule rule1 = Rule.withHead(S).addSymbols(S, S, S).build();
		Rule rule2 = Rule.withHead(S).addSymbols(S, S).build();
		Rule rule3 = Rule.withHead(S).addSymbols(b).build();
		return Grammar.builder().addRules(rule1, rule2, rule3).build();
	}
	
	private static Nonterminal getStartSymbol() {
		return Nonterminal.withName("S");
	}
	
	private static Input getInput1() {
		return Input.fromString("bbb");
	}
	
	private static Input getInput2() {
		return Input.fromString("bbbb");
	}
	
	private static Input getInput3() {
		return Input.fromString("bbbbb");
	}
	
	private static ParseSuccess getNewParseResult1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(31)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(23)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(12)
				.setAmbiguousNodesCount(2).build();
		return new ParseSuccess(expectedSPPF1(graph), statistics);
	}
	
	private static ParseSuccess getOriginalParseResult1(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(118)
				.setGSSNodesCount(17)
				.setGSSEdgesCount(88)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(3)
				.setPackedNodesCount(12)
				.setAmbiguousNodesCount(2).build();
		return new ParseSuccess(expectedSPPF1(graph), statistics);
	}
	
	private static ParseSuccess getNewParseResult2(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(50)
				.setGSSNodesCount(5)
				.setGSSEdgesCount(36)
				.setNonterminalNodesCount(10)
				.setTerminalNodesCount(4)
				.setIntermediateNodesCount(6)
				.setPackedNodesCount(28)
				.setAmbiguousNodesCount(6).build();
		return new ParseSuccess(expectedSPPF2(graph), statistics);
	}
	
	private static ParseSuccess getOriginalParseResult2(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(198)
				.setGSSNodesCount(22)
				.setGSSEdgesCount(144)
				.setNonterminalNodesCount(10)
				.setTerminalNodesCount(4)
				.setIntermediateNodesCount(6)
				.setPackedNodesCount(28)
				.setAmbiguousNodesCount(6).build();
		return new ParseSuccess(expectedSPPF2(graph), statistics);
	}
	
	private static ParseSuccess getNewParseResult3(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(74)
				.setGSSNodesCount(6)
				.setGSSEdgesCount(52)
				.setNonterminalNodesCount(15)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(10)
				.setPackedNodesCount(55)
				.setAmbiguousNodesCount(12).build();
		return new ParseSuccess(expectedSPPF3(graph), statistics);
	}
	
	private static ParseSuccess getOriginalParseResult3(GrammarGraph graph) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(303)
				.setGSSNodesCount(27)
				.setGSSEdgesCount(215)
				.setNonterminalNodesCount(15)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(10)
				.setPackedNodesCount(55)
				.setAmbiguousNodesCount(12).build();
		return new ParseSuccess(expectedSPPF3(graph), statistics);
	}
	
	private static NonterminalNode expectedSPPF1(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3);
		PackedNode node2 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= S S . S", 0, 2);
		PackedNode node4 = factory.createPackedNode("S ::= S S . S", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 1);
		PackedNode node6 = factory.createPackedNode("S ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2);
		PackedNode node9 = factory.createPackedNode("S ::= b .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 3);
		PackedNode node12 = factory.createPackedNode("S ::= b .", 3, node11);
		TerminalNode node13 = factory.createTerminalNode("b", 2, 3);
		node12.addChild(node13);
		node11.addChild(node12);
		node2.addChild(node3);
		node2.addChild(node11);
		PackedNode node14 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node16 = factory.createNonterminalNode("S", 0, 1, 3);
		PackedNode node17 = factory.createPackedNode("S ::= S S .", 2, node16);
		node17.addChild(node8);
		node17.addChild(node11);
		node16.addChild(node17);
		node14.addChild(node5);
		node14.addChild(node16);
		PackedNode node20 = factory.createPackedNode("S ::= S S .", 2, node1);
		NonterminalNode node21 = factory.createNonterminalNode("S", 0, 0, 2);
		PackedNode node22 = factory.createPackedNode("S ::= S S .", 1, node21);
		node22.addChild(node5);
		node22.addChild(node8);
		node21.addChild(node22);
		node20.addChild(node21);
		node20.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node14);
		node1.addChild(node20);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF2(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 4);
		PackedNode node2 = factory.createPackedNode("S ::= S S S .", 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= S S . S", 0, 3);
		PackedNode node4 = factory.createPackedNode("S ::= S S . S", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("S", 0, 0, 1);
		PackedNode node6 = factory.createPackedNode("S ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 3);
		PackedNode node9 = factory.createPackedNode("S ::= S S .", 2, node8);
		NonterminalNode node10 = factory.createNonterminalNode("S", 0, 1, 2);
		PackedNode node11 = factory.createPackedNode("S ::= b .", 2, node10);
		TerminalNode node12 = factory.createTerminalNode("b", 1, 2);
		node11.addChild(node12);
		node10.addChild(node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3);
		PackedNode node14 = factory.createPackedNode("S ::= b .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("b", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		node9.addChild(node10);
		node9.addChild(node13);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		PackedNode node16 = factory.createPackedNode("S ::= S S . S", 2, node3);
		NonterminalNode node17 = factory.createNonterminalNode("S", 0, 0, 2);
		PackedNode node18 = factory.createPackedNode("S ::= S S .", 1, node17);
		node18.addChild(node5);
		node18.addChild(node10);
		node17.addChild(node18);
		node16.addChild(node17);
		node16.addChild(node13);
		node3.addChild(node4);
		node3.addChild(node16);
		NonterminalNode node22 = factory.createNonterminalNode("S", 0, 3, 4);
		PackedNode node23 = factory.createPackedNode("S ::= b .", 4, node22);
		TerminalNode node24 = factory.createTerminalNode("b", 3, 4);
		node23.addChild(node24);
		node22.addChild(node23);
		node2.addChild(node3);
		node2.addChild(node22);
		PackedNode node25 = factory.createPackedNode("S ::= S S S .", 2, node1);
		IntermediateNode node26 = factory.createIntermediateNode("S ::= S S . S", 0, 2);
		PackedNode node27 = factory.createPackedNode("S ::= S S . S", 1, node26);
		node27.addChild(node5);
		node27.addChild(node10);
		node26.addChild(node27);
		NonterminalNode node30 = factory.createNonterminalNode("S", 0, 2, 4);
		PackedNode node31 = factory.createPackedNode("S ::= S S .", 3, node30);
		node31.addChild(node13);
		node31.addChild(node22);
		node30.addChild(node31);
		node25.addChild(node26);
		node25.addChild(node30);
		PackedNode node34 = factory.createPackedNode("S ::= S S .", 3, node1);
		NonterminalNode node35 = factory.createNonterminalNode("S", 0, 0, 3);
		PackedNode node36 = factory.createPackedNode("S ::= S S S .", 2, node35);
		node36.addChild(node26);
		node36.addChild(node13);
		PackedNode node39 = factory.createPackedNode("S ::= S S .", 1, node35);
		node39.addChild(node5);
		node39.addChild(node8);
		PackedNode node42 = factory.createPackedNode("S ::= S S .", 2, node35);
		node42.addChild(node17);
		node42.addChild(node13);
		node35.addChild(node36);
		node35.addChild(node39);
		node35.addChild(node42);
		node34.addChild(node35);
		node34.addChild(node22);
		PackedNode node46 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node48 = factory.createNonterminalNode("S", 0, 1, 4);
		PackedNode node49 = factory.createPackedNode("S ::= S S S .", 3, node48);
		IntermediateNode node50 = factory.createIntermediateNode("S ::= S S . S", 1, 3);
		PackedNode node51 = factory.createPackedNode("S ::= S S . S", 2, node50);
		node51.addChild(node10);
		node51.addChild(node13);
		node50.addChild(node51);
		node49.addChild(node50);
		node49.addChild(node22);
		PackedNode node55 = factory.createPackedNode("S ::= S S .", 2, node48);
		node55.addChild(node10);
		node55.addChild(node30);
		PackedNode node58 = factory.createPackedNode("S ::= S S .", 3, node48);
		node58.addChild(node8);
		node58.addChild(node22);
		node48.addChild(node49);
		node48.addChild(node55);
		node48.addChild(node58);
		node46.addChild(node5);
		node46.addChild(node48);
		PackedNode node61 = factory.createPackedNode("S ::= S S .", 2, node1);
		node61.addChild(node17);
		node61.addChild(node30);
		node1.addChild(node2);
		node1.addChild(node25);
		node1.addChild(node34);
		node1.addChild(node46);
		node1.addChild(node61);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF3(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5);
		PackedNode node2 = factory.createPackedNode("S ::= S S .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("S", 0, 0, 1);
		PackedNode node4 = factory.createPackedNode("S ::= b .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 5);
		PackedNode node7 = factory.createPackedNode("S ::= S S .", 2, node6);
		NonterminalNode node8 = factory.createNonterminalNode("S", 0, 1, 2);
		PackedNode node9 = factory.createPackedNode("S ::= b .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		NonterminalNode node11 = factory.createNonterminalNode("S", 0, 2, 5);
		PackedNode node12 = factory.createPackedNode("S ::= S S .", 3, node11);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3);
		PackedNode node14 = factory.createPackedNode("S ::= b .", 3, node13);
		TerminalNode node15 = factory.createTerminalNode("b", 2, 3);
		node14.addChild(node15);
		node13.addChild(node14);
		NonterminalNode node16 = factory.createNonterminalNode("S", 0, 3, 5);
		PackedNode node17 = factory.createPackedNode("S ::= S S .", 4, node16);
		NonterminalNode node18 = factory.createNonterminalNode("S", 0, 3, 4);
		PackedNode node19 = factory.createPackedNode("S ::= b .", 4, node18);
		TerminalNode node20 = factory.createTerminalNode("b", 3, 4);
		node19.addChild(node20);
		node18.addChild(node19);
		NonterminalNode node21 = factory.createNonterminalNode("S", 0, 4, 5);
		PackedNode node22 = factory.createPackedNode("S ::= b .", 5, node21);
		TerminalNode node23 = factory.createTerminalNode("b", 4, 5);
		node22.addChild(node23);
		node21.addChild(node22);
		node17.addChild(node18);
		node17.addChild(node21);
		node16.addChild(node17);
		node12.addChild(node13);
		node12.addChild(node16);
		PackedNode node24 = factory.createPackedNode("S ::= S S .", 4, node11);
		NonterminalNode node25 = factory.createNonterminalNode("S", 0, 2, 4);
		PackedNode node26 = factory.createPackedNode("S ::= S S .", 3, node25);
		node26.addChild(node13);
		node26.addChild(node18);
		node25.addChild(node26);
		node24.addChild(node25);
		node24.addChild(node21);
		PackedNode node30 = factory.createPackedNode("S ::= S S S .", 4, node11);
		IntermediateNode node31 = factory.createIntermediateNode("S ::= S S . S", 2, 4);
		PackedNode node32 = factory.createPackedNode("S ::= S S . S", 3, node31);
		node32.addChild(node13);
		node32.addChild(node18);
		node31.addChild(node32);
		node30.addChild(node31);
		node30.addChild(node21);
		node11.addChild(node12);
		node11.addChild(node24);
		node11.addChild(node30);
		node7.addChild(node8);
		node7.addChild(node11);
		PackedNode node36 = factory.createPackedNode("S ::= S S .", 4, node6);
		NonterminalNode node37 = factory.createNonterminalNode("S", 0, 1, 4);
		PackedNode node38 = factory.createPackedNode("S ::= S S .", 2, node37);
		node38.addChild(node8);
		node38.addChild(node25);
		PackedNode node41 = factory.createPackedNode("S ::= S S .", 3, node37);
		NonterminalNode node42 = factory.createNonterminalNode("S", 0, 1, 3);
		PackedNode node43 = factory.createPackedNode("S ::= S S .", 2, node42);
		node43.addChild(node8);
		node43.addChild(node13);
		node42.addChild(node43);
		node41.addChild(node42);
		node41.addChild(node18);
		PackedNode node47 = factory.createPackedNode("S ::= S S S .", 3, node37);
		IntermediateNode node48 = factory.createIntermediateNode("S ::= S S . S", 1, 3);
		PackedNode node49 = factory.createPackedNode("S ::= S S . S", 2, node48);
		node49.addChild(node8);
		node49.addChild(node13);
		node48.addChild(node49);
		node47.addChild(node48);
		node47.addChild(node18);
		node37.addChild(node38);
		node37.addChild(node41);
		node37.addChild(node47);
		node36.addChild(node37);
		node36.addChild(node21);
		PackedNode node54 = factory.createPackedNode("S ::= S S .", 3, node6);
		node54.addChild(node42);
		node54.addChild(node16);
		PackedNode node57 = factory.createPackedNode("S ::= S S S .", 4, node6);
		IntermediateNode node58 = factory.createIntermediateNode("S ::= S S . S", 1, 4);
		PackedNode node59 = factory.createPackedNode("S ::= S S . S", 3, node58);
		node59.addChild(node42);
		node59.addChild(node18);
		PackedNode node62 = factory.createPackedNode("S ::= S S . S", 2, node58);
		node62.addChild(node8);
		node62.addChild(node25);
		node58.addChild(node59);
		node58.addChild(node62);
		node57.addChild(node58);
		node57.addChild(node21);
		PackedNode node66 = factory.createPackedNode("S ::= S S S .", 3, node6);
		node66.addChild(node48);
		node66.addChild(node16);
		node6.addChild(node7);
		node6.addChild(node36);
		node6.addChild(node54);
		node6.addChild(node57);
		node6.addChild(node66);
		node2.addChild(node3);
		node2.addChild(node6);
		PackedNode node69 = factory.createPackedNode("S ::= S S .", 4, node1);
		NonterminalNode node70 = factory.createNonterminalNode("S", 0, 0, 4);
		PackedNode node71 = factory.createPackedNode("S ::= S S .", 1, node70);
		node71.addChild(node3);
		node71.addChild(node37);
		PackedNode node74 = factory.createPackedNode("S ::= S S .", 3, node70);
		NonterminalNode node75 = factory.createNonterminalNode("S", 0, 0, 3);
		PackedNode node76 = factory.createPackedNode("S ::= S S .", 1, node75);
		node76.addChild(node3);
		node76.addChild(node42);
		PackedNode node79 = factory.createPackedNode("S ::= S S .", 2, node75);
		NonterminalNode node80 = factory.createNonterminalNode("S", 0, 0, 2);
		PackedNode node81 = factory.createPackedNode("S ::= S S .", 1, node80);
		node81.addChild(node3);
		node81.addChild(node8);
		node80.addChild(node81);
		node79.addChild(node80);
		node79.addChild(node13);
		PackedNode node85 = factory.createPackedNode("S ::= S S S .", 2, node75);
		IntermediateNode node86 = factory.createIntermediateNode("S ::= S S . S", 0, 2);
		PackedNode node87 = factory.createPackedNode("S ::= S S . S", 1, node86);
		node87.addChild(node3);
		node87.addChild(node8);
		node86.addChild(node87);
		node85.addChild(node86);
		node85.addChild(node13);
		node75.addChild(node76);
		node75.addChild(node79);
		node75.addChild(node85);
		node74.addChild(node75);
		node74.addChild(node18);
		PackedNode node92 = factory.createPackedNode("S ::= S S .", 2, node70);
		node92.addChild(node80);
		node92.addChild(node25);
		PackedNode node95 = factory.createPackedNode("S ::= S S S .", 3, node70);
		IntermediateNode node96 = factory.createIntermediateNode("S ::= S S . S", 0, 3);
		PackedNode node97 = factory.createPackedNode("S ::= S S . S", 2, node96);
		node97.addChild(node80);
		node97.addChild(node13);
		PackedNode node100 = factory.createPackedNode("S ::= S S . S", 1, node96);
		node100.addChild(node3);
		node100.addChild(node42);
		node96.addChild(node97);
		node96.addChild(node100);
		node95.addChild(node96);
		node95.addChild(node18);
		PackedNode node104 = factory.createPackedNode("S ::= S S S .", 2, node70);
		node104.addChild(node86);
		node104.addChild(node25);
		node70.addChild(node71);
		node70.addChild(node74);
		node70.addChild(node92);
		node70.addChild(node95);
		node70.addChild(node104);
		node69.addChild(node70);
		node69.addChild(node21);
		PackedNode node108 = factory.createPackedNode("S ::= S S .", 3, node1);
		node108.addChild(node75);
		node108.addChild(node16);
		PackedNode node111 = factory.createPackedNode("S ::= S S S .", 4, node1);
		IntermediateNode node112 = factory.createIntermediateNode("S ::= S S . S", 0, 4);
		PackedNode node113 = factory.createPackedNode("S ::= S S . S", 3, node112);
		node113.addChild(node75);
		node113.addChild(node18);
		PackedNode node116 = factory.createPackedNode("S ::= S S . S", 2, node112);
		node116.addChild(node80);
		node116.addChild(node25);
		PackedNode node119 = factory.createPackedNode("S ::= S S . S", 1, node112);
		node119.addChild(node3);
		node119.addChild(node37);
		node112.addChild(node113);
		node112.addChild(node116);
		node112.addChild(node119);
		node111.addChild(node112);
		node111.addChild(node21);
		PackedNode node123 = factory.createPackedNode("S ::= S S .", 2, node1);
		node123.addChild(node80);
		node123.addChild(node11);
		PackedNode node126 = factory.createPackedNode("S ::= S S S .", 3, node1);
		node126.addChild(node96);
		node126.addChild(node16);
		PackedNode node129 = factory.createPackedNode("S ::= S S S .", 2, node1);
		node129.addChild(node86);
		node129.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node69);
		node1.addChild(node108);
		node1.addChild(node111);
		node1.addChild(node123);
		node1.addChild(node126);
		node1.addChild(node129);
		return node1;
	}

}
