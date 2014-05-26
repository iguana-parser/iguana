package org.jgll.parser.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test9 {

	private GrammarGraph grammar;
	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');

	
	@Before
	public void init() {
		
		Rule r1 = new Rule(S, list(A, A, b));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(A);
		
		grammar = new Grammar().addRule(r1).addRule(r3).addRule(r4).toGrammarGraph();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}
	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 1, 0, 2);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(A,A), 0, 1);
		PackedNode node3 = new PackedNode(grammar.getIntermediateNodeId(A,A), 0, node2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 0);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 1);
		TokenSymbolNode node6 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		PackedNode node7 = new PackedNode(grammar.getIntermediateNodeId(A,A), 1, node2);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 1, 1);
		node7.addChild(node5);
		node7.addChild(node8);
		node2.addChild(node3);
		node2.addChild(node7);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 1, 1);
		node1.addChild(node2);
		node1.addChild(node9);
		return node1;
	}
}
	