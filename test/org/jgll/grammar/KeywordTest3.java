package org.jgll.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.BitSetUtil;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= "if" L S L "then" L S 
 *     | s
 *     
 * L ::= " "    
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest3 {
	
	private Grammar grammar;
	private GLLParser parser;
	
	Nonterminal S = new Nonterminal("S");
	Keyword iff = new Keyword("if", new int[] {'i', 'f'});
	Keyword then = new Keyword("then", new int[] {'t', 'h', 'e', 'n'});
	Nonterminal L = new Nonterminal("L");
	Character s = new Character('s');
	Character ws = new Character(' ');

	@Before
	public void init() {
		
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		grammar = new GrammarBuilder().addRule(r1)
								   .addRule(r2)
								   .addRule(r3).build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(BitSetUtil.from(grammar.getTokenID(iff), grammar.getTokenID(s)), grammar.getNonterminalByName("S").getFirstSet());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("if s then s");
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF1()));
	}
		
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 11);
		TokenSymbolNode node2 = new TokenSymbolNode(2, 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 2, 3);
		TokenSymbolNode node4 = new TokenSymbolNode(5, 2, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 4);
		TokenSymbolNode node6 = new TokenSymbolNode(4, 3, 1);
		node5.addChild(node6);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 4, 5);
		TokenSymbolNode node8 = new TokenSymbolNode(5, 4, 1);
		node7.addChild(node8);
		TokenSymbolNode node9 = new TokenSymbolNode(3, 5, 4);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 9, 10);
		TokenSymbolNode node11 = new TokenSymbolNode(5, 9, 1);
		node10.addChild(node11);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 10, 11);
		TokenSymbolNode node13 = new TokenSymbolNode(4, 10, 1);
		node12.addChild(node13);
		node1.addChild(node2);
		node1.addChild(node3);
		node1.addChild(node5);
		node1.addChild(node7);
		node1.addChild(node9);
		node1.addChild(node10);
		node1.addChild(node12);
		return node1;
	}
	
}
