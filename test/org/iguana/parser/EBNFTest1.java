package org.iguana.parser;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Plus;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
