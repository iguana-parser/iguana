package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.condition.*;
import org.jgll.grammar.ebnf.*;
import org.jgll.grammar.symbol.*;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.*;
import org.jgll.sppf.*;
import org.jgll.util.*;
import org.junit.*;

/**
 * 
 * S ::= a S b S
 *     | a S \ a S b S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar2 {

	private GrammarGraph grammarGraph;
	
	private Nonterminal S = new Nonterminal("S");
	private Character s = Character.from('s');
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Group group = Group.of(a, S);

	
	@Before
	public void createGrammar() {
		
		Grammar grammar = new Grammar();
		
		Rule rule1 = new Rule(S, list(group.withCondition(ContextFreeCondition.notMatch(a, S, b, S))));
		grammar.addRules(EBNFUtil.rewrite(rule1));
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		grammar.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		grammar.addRule(rule3);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF()));
	}
	
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(a, S, b), 0, 4);
		IntermediateNode node3 = new IntermediateNode(grammarGraph.getIntermediateNodeId(a, S), 0, 3);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 1, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(group), 1, 1, 3);
		TokenSymbolNode node7 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		TokenSymbolNode node10 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 3, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 3, 4, 5);
		TokenSymbolNode node12 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(s), 4, 1);
		node11.addChild(node12);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}

}
