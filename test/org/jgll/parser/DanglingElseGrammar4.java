package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= a S b
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar4 {

	Nonterminal S = Nonterminal.withName("S");
	Character s = Character.from('s');
	Character a = Character.from('a');
	Character b = Character.from('b');
	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = new Rule(S, list(a, S, b));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aasb");
		GLLParser parser = ParserFactory.originalParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		Visualization.generateGSSGraph("/Users/aliafroozeh/output", parser.getGSSLookup().getGSSNodes());
//		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getSPPFNode(), grammar.toGrammarGraph(), input);
	}
	
	private SPPFNode getExpectedSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 5);
		TokenSymbolNode node2 = factory.createTokenNode(a, 0, 1);
		NonterminalNode node3 = factory.createNonterminalNode(S, 1, 5);
		IntermediateNode node4 = factory.createIntermediateNode(list(a, S, b), 1, 4);
		IntermediateNode node5 = factory.createIntermediateNode(list(a, S), 1, 3);
		TokenSymbolNode node6 = factory.createTokenNode(a, 1, 1);
		NonterminalNode node7 = factory.createNonterminalNode(S, 2, 3);
		TokenSymbolNode node8 = factory.createTokenNode(s, 2, 1);
		node7.addChild(node8);
		node5.addChild(node6);
		node5.addChild(node7);
		TokenSymbolNode node9 = factory.createTokenNode(b, 3, 1);
		node4.addChild(node5);
		node4.addChild(node9);
		NonterminalNode node10 = factory.createNonterminalNode(S, 4, 5);
		TokenSymbolNode node11 = factory.createTokenNode(s, 4, 1);
		node10.addChild(node11);
		node3.addChild(node4);
		node3.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}

}
