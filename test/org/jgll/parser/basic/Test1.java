package org.jgll.parser.basic;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
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

	private Nonterminal A = Nonterminal.withName("A");

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
	public void testSPPF() {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(expectedSPPF()));
	}
	
	
	private SPPFNode expectedSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 0);
		return node1;
	}

}