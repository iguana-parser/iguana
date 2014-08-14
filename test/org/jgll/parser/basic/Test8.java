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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A B C
 *     | A B D
 *     
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test8 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Nonterminal D = Nonterminal.withName("D");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	@Before
	public void init() {
		Rule r1 = new Rule(S, list(A, B, C));
		Rule r2 = new Rule(S, list(A, B, D));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		Rule r6 = new Rule(D, list(c));
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).build();
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);
		NonterminalNode node1 = factory.createNonterminalNode(S, 0, 3);
		PackedNode node2 = new PackedNode(grammarGraph.getPackedNodeId(S, A, B, C), 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode(list(A, B), 0, 2);
		NonterminalNode node4 = factory.createNonterminalNode(A, 0, 1);
		TokenSymbolNode node5 = factory.createTokenNode(a, 0, 1);
		node4.addChild(node5);
		NonterminalNode node6 = factory.createNonterminalNode(B, 1, 2);
		TokenSymbolNode node7 = factory.createTokenNode(b, 1, 1);
		node6.addChild(node7);
		node3.addChild(node4);
		node3.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode(C, 2, 3);
		TokenSymbolNode node9 = factory.createTokenNode(c, 2, 1);
		node8.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node8);
		PackedNode node10 = new PackedNode(grammarGraph.getPackedNodeId(S, A, B, D), 2, node1);
		NonterminalNode node11 = factory.createNonterminalNode(D, 2, 3);
		node11.addChild(node9);
		node10.addChild(node3);
		node10.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
}
	