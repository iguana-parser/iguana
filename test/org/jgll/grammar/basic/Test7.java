package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= 'a' A 'c'
 *     | 'a' A 'b'
 *     
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test7 {

	private GrammarGraph grammar;
	
	private Nonterminal S = new Nonterminal("S");
	private Nonterminal A = new Nonterminal("A");
	
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	@Before
	public void init() {
		Rule r1 = new Rule(S, list(a, A, c));
		Rule r2 = new Rule(S, list(a, A, b));
		Rule r3 = new Rule(A, list(a));
		
		grammar = new Grammar().addRule(r1).addRule(r2).addRule(r3).toGrammarGraph();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getSPPF()));
	}	

	
	private SPPFNode getSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 2, 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(a, A), 0, 2);
		TokenSymbolNode node3 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 1, 2);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 2, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}
}
	