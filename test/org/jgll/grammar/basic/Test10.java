package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;

import java.util.ArrayList;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
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
	
	private GrammarGraph grammarGraph;
	
	private Nonterminal A = new Nonterminal("A");

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(A);

		grammarGraph = new Grammar().addRule(r1)
									.addRule(r2)
									.addRule(r3).toGrammarGraph();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
		
		// TODO: stackoverflow bug due to the cycle in the SPPF. Fix it later!
//		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void test2() throws ParseError {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
	}

	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 3, 0, 0);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(A, new ArrayList<Symbol>()), 0, node1);
		PackedNode node3 = new PackedNode(grammarGraph.getPackedNodeId(A, A, A), 0, node1);
		node3.addChild(node1);
		node3.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}
}
