package org.jgll.grammar.basic;

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
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= 'a' 'b'
 * 
 * @author Ali Afroozeh
 */
public class Test3 {

	private GrammarGraph grammarGraph;

	private Nonterminal A = new Nonterminal("A");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(a, b));
		
		grammarGraph = new Grammar().addRule(r1).toGrammarGraph();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammarGraph.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(A), 1, 0, 2);
		TokenSymbolNode node2 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(a), 0, 1);
		TokenSymbolNode node3 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(b), 1, 2);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}

}