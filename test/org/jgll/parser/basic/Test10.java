package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Test10 {
	
	private Nonterminal A = Nonterminal.withName("A");
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(Character.from('a')));
		Rule r3 = new Rule(A);

		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
		
		// TODO: stackoverflow bug due to the cycle in the SPPF. Fix it later!
//		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "A");
		assertTrue(result.isParseSuccess());
	}

	
	private SPPFNode getSPPF1() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		NonterminalNode node1 = new NonterminalNode(grammarGraph.getNonterminalId(A), 3, 0, 0);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(A, new ArrayList<Symbol>()), 0, node1);
		PackedNode node3 = new PackedNode(grammarGraph.getPackedNodeId(A, A, A), 0, node1);
		node3.addChild(node1);
		node3.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}
}
