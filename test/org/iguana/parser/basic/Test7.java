package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgll.AbstractParserTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.operations.FirstFollowSets;
import org.jgll.grammar.operations.ReachabilityGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParseSuccess;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableSet;

/**
 * A ::= B C D
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'd'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test7 extends AbstractParserTest {

	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Nonterminal C = Nonterminal.withName("C");
	static Nonterminal D = Nonterminal.withName("D");
	static Character b = Character.from('b');
	static Character c = Character.from('c');
	static Character d = Character.from('d');
	
	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test7::getParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Input getInput() {
    	return Input.fromString("bcd");
    }
    
    private static Nonterminal getStartSymbol() {
    	return A;
    }
	
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(B, C, D).build();
		Rule r2 = Rule.withHead(B).addSymbol(b).build();
		Rule r3 = Rule.withHead(C).addSymbol(c).build();
		Rule r4 = Rule.withHead(D).addSymbol(d).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(B));
		assertFalse(firstFollowSets.isNullable(C));
		assertFalse(firstFollowSets.isNullable(D));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(B, C, D), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(B));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(C));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(D));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}

	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(7)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(4)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(5)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 3);
		PackedNode node2 = factory.createPackedNode("A ::= B C D .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= B C . D", 0, 2);
		PackedNode node4 = factory.createPackedNode("A ::= B C . D", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("B", 0, 0, 1);
		PackedNode node6 = factory.createPackedNode("B ::= b .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("b", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("C", 0, 1, 2);
		PackedNode node9 = factory.createPackedNode("C ::= c .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("c", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("D", 0, 2, 3);
		PackedNode node12 = factory.createPackedNode("D ::= d .", 3, node11);
		TerminalNode node13 = factory.createTerminalNode("d", 2, 3);
		node12.addChild(node13);
		node11.addChild(node12);
		node2.addChild(node3);
		node2.addChild(node11);
		node1.addChild(node2);
		return node1;
	}
	
}