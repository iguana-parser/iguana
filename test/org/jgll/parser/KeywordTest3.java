package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= "if" L S L "then" L S 
 *     | s
 *     
 * L ::= " "    
 * 
 * @author Ali Afroozeh
 *
 */

public class KeywordTest3 {
	
	private GrammarGraph grammarGraph;
	
	Nonterminal S = Nonterminal.withName("S");
	Keyword iff = Keyword.from("if");
	Keyword then = Keyword.from("then");
	Nonterminal L = Nonterminal.withName("L");
	Character s = Character.from('s');
	Character ws = Character.from(' ');

	@Before
	public void init() {
		
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		grammarGraph = new Grammar().addRule(r1)
										     .addRule(r2)
  										     .addRule(r3).toGrammarGraph();
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff, s), grammarGraph.getFirstSet(S));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("if s then s");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
		
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalSymbolNode node1 = factory.createNonterminalNode(S, 0, 11);
		IntermediateNode node2 = factory.createIntermediateNode(list(iff, L, S, L, then, L), 0, 10);
		IntermediateNode node3 = factory.createIntermediateNode(list(iff, L, S, L, then), 0, 9);
		IntermediateNode node4 = factory.createIntermediateNode(list(iff, L, S, L), 0, 5);
		IntermediateNode node5 = factory.createIntermediateNode(list(iff, L, S), 0, 4);
		IntermediateNode node6 = factory.createIntermediateNode(list(iff, L), 0, 3);
		TokenSymbolNode node7 = factory.createTokenNode(iff, 0, 2);
		NonterminalSymbolNode node8 = factory.createNonterminalNode(L, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(ws, 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		NonterminalSymbolNode node10 = factory.createNonterminalNode(S, 3, 4);
		TokenSymbolNode node11 = factory.createTokenNode(s, 3, 1);
		node10.addChild(node11);
		node5.addChild(node6);
		node5.addChild(node10);
		NonterminalSymbolNode node12 = factory.createNonterminalNode(L, 4, 5);
		TokenSymbolNode node13 = factory.createTokenNode(ws, 4, 1);
		node12.addChild(node13);
		node4.addChild(node5);
		node4.addChild(node12);
		TokenSymbolNode node14 = factory.createTokenNode(then, 5, 4);
		node3.addChild(node4);
		node3.addChild(node14);
		NonterminalSymbolNode node15 = factory.createNonterminalNode(L, 9, 10);
		TokenSymbolNode node16 = factory.createTokenNode(ws, 9, 1);
		node15.addChild(node16);
		node2.addChild(node3);
		node2.addChild(node15);
		NonterminalSymbolNode node17 = factory.createNonterminalNode(S, 10, 11);
		TokenSymbolNode node18 = factory.createTokenNode(s, 10, 1);
		node17.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node17);
		return node1;
	}
	
}
