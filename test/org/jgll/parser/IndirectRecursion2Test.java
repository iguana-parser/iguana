package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A d | a
 * 
 * B ::= epsilon | b
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion2Test {

	private GrammarGraph grammarGraph;
	
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");

	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character d = Character.from('d');

	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, A, d));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B);
		Rule r4 = new Rule(B, list(b));

		grammarGraph = new Grammar().addRule(r1)
								     		  .addRule(r2)
								     		  .addRule(r3)
								     		  .addRule(r4).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
		assertTrue(grammarGraph.getHeadGrammarSlot("B").isNullable());
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ad");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(expectedSPPF()));
	}
	
	@Test
	//TODO: fix it
	public void testReachabilityGraph() {
//		Set<HeadGrammarSlot> set = builder.getDirectReachableNonterminals("A");
//		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("A")));
//		assertTrue(set.contains(grammarGraph.getHeadGrammarSlot("B")));
	}
	
	private SPPFNode expectedSPPF() {		
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 2, 0, 2);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(B, A), 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 2, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(d), 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}