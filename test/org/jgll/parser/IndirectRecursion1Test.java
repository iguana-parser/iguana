package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.ToJavaCode;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B C | a
 * 
 * B ::= A | b
 * 
 * C ::= c
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion1Test {

	private GrammarGraph grammarGraph;

	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	Character a = Character.from('a');
	Character b = Character.from('b');
	Character c = Character.from('c');

	@Before
	public void createGrammar() {
		Rule r1 = new Rule(A, list(B, C));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B, list(A));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		
		grammarGraph = new Grammar().addRule(r1).addRule(r2).addRule(r3)
								  .addRule(r4).addRule(r5).toGrammarGraph();
	}
	
	
	@Test
	public void testFirstFollowSets() {
		assertEquals(set(a, b), grammarGraph.getFirstSet(A));
		assertEquals(set(a, b), grammarGraph.getFirstSet(B));
		assertEquals(set(c), grammarGraph.getFirstSet(C));
		
		assertEquals(set(c, EOF.getInstance()), grammarGraph.getFollowSet(A));
		assertEquals(set(c, EOF.getInstance()), grammarGraph.getFollowSet(B));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("bc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		System.out.println(ToJavaCode.toJavaCode((NonterminalSymbolNode) result.asParseSuccess().getSPPFNode(), grammarGraph));
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(expectedSPPF()));
	}

	
	private SPPFNode expectedSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(A, 0, 2);
		NonterminalSymbolNode node2 = factory.createNonterminalNode(B, 0, 1);
		TokenSymbolNode node3 = factory.createTokenNode(b, 0, 1);
		node2.addChild(node3);
		NonterminalSymbolNode node4 = factory.createNonterminalNode(C, 1, 2);
		TokenSymbolNode node5 = factory.createTokenNode(c, 1, 1);
		node4.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}

}