package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
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

	private GrammarGraph grammar;
	
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Nonterminal C = new Nonterminal("C");
	private Nonterminal D = new Nonterminal("D");
	
	private Character a = new Character('a');
	private Character b = new Character('b');
	private Character c = new Character('c');

	
	@Before
	public void init() {
		Rule r1 = new Rule(S, list(A, B, C));
		Rule r2 = new Rule(S, list(A, B, D));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(B, list(b));
		Rule r5 = new Rule(C, list(c));
		Rule r6 = new Rule(D, list(c));
		
		grammar = new Grammar().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).toGrammarGraph();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, grammar, input);
		assertTrue(sppf.deepEquals(getSPPF()));
	}
	
	public SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 2, 0, 3);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(S, A, B, D), 2, node1);
		IntermediateNode node3 = new IntermediateNode(grammar.getIntermediateNodeId(A,B), 0, 2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node4.addChild(node5);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 1, 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 1, 1);
		node6.addChild(node7);
		node3.addChild(node4);
		node3.addChild(node6);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(D), 1, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(c), 2, 1);
		node8.addChild(node9);
		node2.addChild(node3);
		node2.addChild(node8);
		PackedNode node10 = new PackedNode(grammar.getPackedNodeId(S, A, B, C), 2, node1);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalId(C), 1, 2, 3);
		node11.addChild(node9);
		node10.addChild(node3);
		node10.addChild(node11);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
}
	