package org.iguana.parser.basic;

import static org.iguana.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
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
import org.iguana.sppf.IntermediateNode;
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
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test11 extends AbstractParserTest {
	
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	
	@Parameters
    public static Collection<Object[]> data() {
		 List<Object[]> parameters = newConfigs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test11::getNewParseResult
	    	}).collect(Collectors.toList());
		 parameters.addAll(originalConfigs.stream().map(c -> new Object[] {
		    		getInput(), 
		    		getGrammar(), 
		    		getStartSymbol(),
		    		ParserFactory.getParser(c, getInput(), getGrammar()),
		    		(Function<GrammarGraph, ParseResult>) Test11::getOriginalParseResult
		    	}).collect(Collectors.toList()));
		 return parameters;
    }
    
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(A), reachabilityGraph.getReachableNonterminals(S));
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(A));
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertTrue(firstFollowSets.isNullable(A));
		assertFalse(firstFollowSets.isNullable(S));
	}
    
    private static Input getInput() {
    	return Input.fromString("ab");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("S");
    }
    
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(S).addSymbols(A, A, b).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).build();
		Rule r3 = Rule.withHead(A).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	private static ParseSuccess getNewParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(9)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(4)
				.setTerminalNodesCount(4)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static ParseSuccess getOriginalParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(11)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(4)
				.setTerminalNodesCount(4)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(7)
				.setAmbiguousNodesCount(1).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2);
		PackedNode node2 = factory.createPackedNode("S ::= A A b .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A A . b", 0, 1);
		PackedNode node4 = factory.createPackedNode("S ::= A A . b", 0, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node6 = factory.createPackedNode("A ::= .", 0, node5);
		TerminalNode node7 = factory.createEpsilonNode(0);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node9 = factory.createPackedNode("A ::= a .", 1, node8);
		TerminalNode node10 = factory.createTerminalNode("a", 0, 1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		PackedNode node11 = factory.createPackedNode("S ::= A A . b", 1, node3);
		NonterminalNode node13 = factory.createNonterminalNode("A", 0, 1, 1);
		PackedNode node14 = factory.createPackedNode("A ::= .", 1, node13);
		TerminalNode node15 = factory.createEpsilonNode(1);
		node14.addChild(node15);
		node13.addChild(node14);
		node11.addChild(node8);
		node11.addChild(node13);
		node3.addChild(node4);
		node3.addChild(node11);
		TerminalNode node16 = factory.createTerminalNode("b", 1, 2);
		node2.addChild(node3);
		node2.addChild(node16);
		node1.addChild(node2);
		return node1;
	}
}
	