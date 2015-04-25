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
import org.iguana.util.Input;
import org.iguana.util.ParseStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * A ::= a A b
 *	   | a A c
 *     | a
 *     
 * @author Ali Afroozeh
 * 
 */
@RunWith(Parameterized.class)
public class Test16 extends AbstractParserTest {
	
	static Nonterminal A = Nonterminal.withName("A");
	static Character a = Character.from('a');
	static Character b = Character.from('b');
	static Character c  = Character.from('c');

	@Parameters
    public static Collection<Object[]> data() {
		return originalConfigs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test16::getOriginalParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Nonterminal getStartSymbol() {
    	return A;
    }
    
	@Test
	public void testNullable() {
		FirstFollowSets firstFollowSets = new FirstFollowSets(grammar);
		assertFalse(firstFollowSets.isNullable(A));
	}
	
	@Test
	public void testReachableNonterminals() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(A), reachabilityGraph.getReachableNonterminals(A));
	}
	
	public static Grammar getGrammar() {
		Rule r1 = Rule.withHead(A).addSymbols(a, A, b).build();
		Rule r2 = Rule.withHead(A).addSymbols(a, A, c).build();
		Rule r3 = Rule.withHead(A).addSymbols(a).build();
		return Grammar.builder().addRules(r1, r2, r3).build();
	}
	
	private static Input getInput() {
		return Input.fromString("aaabb");
	}
		
	public static ParseSuccess getOriginalParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(29)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(10)
				.setNonterminalNodesCount(5)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(6)
				.setPackedNodesCount(11)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(null, statistics);
	}
	
}
