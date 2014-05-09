package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.Grammar;
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
 * A ::= 'a' 'b' 'c'
 * 
 * @author Ali Afroozeh
 */
public class Test4 {

	private GrammarGraph grammarGraph;

	private Nonterminal A = new Nonterminal("A");
	private Character a = new Character('a');
	private Character b = new Character('b');
	private Character c = new Character('c');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(a, b, c));
		
		grammarGraph = new Grammar().addRule(r1).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("abc");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammarGraph, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 1, 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammarGraph.getIntermediateNodeId(a, b), 0, 2);
		TokenSymbolNode node3 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 1, 1);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(c), 2, 1);
		node1.addChild(node2);
		node1.addChild(node5);		
		return node1;
	}

}