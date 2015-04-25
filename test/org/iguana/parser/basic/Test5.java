package org.iguana.parser.basic;

import static org.iguana.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.iguana.AbstractParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.ParseStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableSet;

/**
 * A ::= B
 * B ::= 'b'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test5 extends AbstractParserTest {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Nonterminal B = Nonterminal.withName("B");
	static Character b = Character.from('b');

	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test5::getParseResult
	    	}).collect(Collectors.toList());
    }
	
    private static Input getInput() {
    	return Input.fromString("b");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
	
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(B).build();
		Rule r2 = Rule.withHead(B).addSymbol(b).build();
		return Grammar.builder().addRule(r1).addRule(r2).build();
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(B));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(B), reachabilityGraph.getReachableNonterminals(A));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(B));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(3)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(1)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(2)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node2 = factory.createPackedNode("A ::= B .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("B", 0, 0, 1);
		PackedNode node4 = factory.createPackedNode("B ::= b .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
	
}