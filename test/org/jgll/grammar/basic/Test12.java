package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::=  B 'a'
 * B ::= 'b'
 * 
 * @author Ali Afroozeh
 */
public class Test12 {

	private GrammarGraph grammarGraph;

	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");
	private Character a = new Character('a');
	private Character b = new Character('b');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, a));
		Rule r2 = new Rule(B, list(b));
		
		grammarGraph = new Grammar().addRule(r1).addRule(r2).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
		assertFalse(grammarGraph.getHeadGrammarSlot("B").isNullable());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("ba");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 1, 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(B), 1, 0, 1);
		TokenSymbolNode node3 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 0, 1);
		node2.addChild(node3);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 1, 1);
		node1.addChild(node2);
		node1.addChild(node4);
		return node1;
	}
}
