package org.jgll.parser;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.operations.ReachabilityGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.regex.Plus;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * S ::= A+
 *      
 * A ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class EBNFTest1 {
	
	private Grammar grammar;
	
	static Nonterminal S = Nonterminal.withName("S");
	static Nonterminal A = Nonterminal.withName("A");
	static Terminal a = Terminal.from(Character.from('a'));


	@Before
	public void init() {
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = Rule.withHead(S).addSymbols(Plus.from(A)).build();
		builder.addRule(rule1);
		Rule rule2 = Rule.withHead(A).addSymbols(a).build();
		builder.addRule(rule2);
		
		grammar = new EBNFToBNF().transform(builder.build());
	}
	
	@Test
	public void testReachability() {
		ReachabilityGraph reachabilityGraph = new ReachabilityGraph(grammar);
		assertEquals(ImmutableSet.of(A, Nonterminal.withName("A+")), reachabilityGraph.getReachableNonterminals(S));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("aaaaaa");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		parser.parse(input, grammar, S);
	}

}
