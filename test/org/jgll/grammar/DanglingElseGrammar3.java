package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= a S b S
 *     | a S !>> b
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar3 {

	private GrammarGraph grammarGraph;
	
	Nonterminal S = new Nonterminal("S");
	Character s = Character.from('s');
	Character a = Character.from('a');
	Character b = Character.from('b');

	@Before
	public void init() {
		
		Grammar grammar = new Grammar();
		
		Rule rule1 = new Rule(S, list(a, S.withCondition(RegularExpressionCondition.notFollow(Character.from('b')))));
		grammar.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		grammar.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		grammar.addRule(rule3);
		
		grammarGraph =  grammar.toGrammarGraph();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF()));
	}
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 0, 5);
		TokenSymbolNode node2 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 5);
		IntermediateNode node4 = new IntermediateNode(grammarGraph.getIntermediateNodeId(a, S, b), 1, 4);
		IntermediateNode node5 = new IntermediateNode(grammarGraph.getIntermediateNodeId(a, S), 1, 3);
		TokenSymbolNode node6 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node8 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 2, 1);
		node7.addChild(node8);
		node5.addChild(node6);
		node5.addChild(node7);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 3, 1);
		node4.addChild(node5);
		node4.addChild(node9);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 4, 5);
		TokenSymbolNode node11 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 4, 1);
		node10.addChild(node11);
		node3.addChild(node4);
		node3.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}

}
