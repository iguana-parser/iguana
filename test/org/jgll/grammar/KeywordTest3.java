package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.sppf.*;
import org.jgll.util.*;
import org.junit.*;

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
	
	Nonterminal S = new Nonterminal("S");
	Keyword iff = Keyword.from("if");
	Keyword then = Keyword.from("then");
	Nonterminal L = new Nonterminal("L");
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
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
		
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 2, 0, 11);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(iff, L, S, L, then, L), 0, 10);
		IntermediateNode node3 = new IntermediateNode(grammarGraph.getIntermediateNodeId(iff, L, S, L, then), 0, 9);
		IntermediateNode node4 = new IntermediateNode(grammarGraph.getIntermediateNodeId(iff, L, S, L), 0, 5);
		IntermediateNode node5 = new IntermediateNode(grammarGraph.getIntermediateNodeId(iff, L, S), 0, 4);
		IntermediateNode node6 = new IntermediateNode(grammarGraph.getIntermediateNodeId(iff, L), 0, 3);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(iff), 0, 2);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(L), 1, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(ws), 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 2, 3, 4);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 3, 1);
		node10.addChild(node11);
		node5.addChild(node6);
		node5.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(L), 1, 4, 5);
		TokenSymbolNode node13 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(ws), 4, 1);
		node12.addChild(node13);
		node4.addChild(node5);
		node4.addChild(node12);
		TokenSymbolNode node14 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(then), 5, 4);
		node3.addChild(node4);
		node3.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(L), 1, 9, 10);
		TokenSymbolNode node16 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(ws), 9, 1);
		node15.addChild(node16);
		node2.addChild(node3);
		node2.addChild(node15);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 2, 10, 11);
		TokenSymbolNode node18 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 10, 1);
		node17.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node17);
		return node1;
	}
	
}
