package org.jgll.grammar.basic;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test1 {
	
	private GrammarGraph grammarGraph;

	private Nonterminal A = new Nonterminal("A");

	@Before
	public void init() {
		Rule r1 = new Rule(A);
		grammarGraph = new Grammar().addRule(r1).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertTrue(grammarGraph.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testSPPF() throws ParseError {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}
	
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 0, 0, 0);
		return node1;
	}

}