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
import org.iguana.grammar.symbol.Terminal;
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
 * 
 * A ::= 'a' 'b' 'c'
 * 
 * @author Ali Afroozeh
 * 
 */
@RunWith(Parameterized.class)
public class Test4 extends AbstractParserTest {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Terminal a = Terminal.from(Character.from('a'));
	static Terminal b = Terminal.from(Character.from('b'));
	static Terminal c = Terminal.from(Character.from('c'));

	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test4::getParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Input getInput() {
    	return Input.fromString("abc");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
	
	private static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(a, b, c).build();
		return Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(Nonterminal.withName("A")));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(), reachabilityGraph.getReachableNonterminals(A));
	}
	
	private static ParseSuccess getParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(1)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(0)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(2)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3);
		PackedNode node2 = factory.createPackedNode("A ::= a b c .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= a b . c", 0, 2);
		PackedNode node4 = factory.createPackedNode("A ::= a b . c", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		TerminalNode node6 = factory.createTerminalNode("b", 1, 2);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node7 = factory.createTerminalNode("c", 2, 3);
		node2.addChild(node3);
		node2.addChild(node7);
		node1.addChild(node2);
		return node1;
	}

}