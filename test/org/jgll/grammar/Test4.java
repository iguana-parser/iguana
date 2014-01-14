package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

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
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test4 {

	private Grammar grammar;
	private GLLParser parser;
	
	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		Rule r1 = new Rule(A, list(a, B, c));
		Rule r2 = new Rule(A, list(C));
		Rule r3 = new Rule(B, list(b));
		Rule r4 = new Rule(C, list(a, C));
		Rule r5 = new Rule(C, list(c));
		
		grammar = new GrammarBuilder("test4").addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("A").isNullable());
		assertFalse(grammar.getNonterminalByName("B").isNullable());
		assertFalse(grammar.getNonterminalByName("C").isNullable());
	}
	
	@Test
	public void testLL1() {
		assertFalse(grammar.getNonterminalByName("A").isLl1SubGrammar());
		assertTrue(grammar.getNonterminalByName("B").isLl1SubGrammar());
		assertTrue(grammar.getNonterminalByName("C").isLl1SubGrammar());
	}
	
	@Test
	public void testParser() throws ParseError {
		NonterminalSymbolNode sppf1 = parser.parse(Input.fromString("abc"), grammar, "A");
		assertTrue(sppf1.deepEquals(getSPPF1()));
		NonterminalSymbolNode sppf2 = parser.parse(Input.fromString("aaaac"), grammar, "A");
		assertTrue(sppf2.deepEquals(getSPPF2()));
	}
	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= [a] B . [c]"), 0, 2);
		TokenSymbolNode node3 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 1, 2);
		TokenSymbolNode node5 = new TokenSymbolNode(4, 1, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(3, 2, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}
	
	private SPPFNode getSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 5);
		TokenSymbolNode node3 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 5);
		TokenSymbolNode node5 = new TokenSymbolNode(2, 1, 1);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 2, 5);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 2, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 3, 5);
		TokenSymbolNode node9 = new TokenSymbolNode(2, 3, 1);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 4, 5);
		TokenSymbolNode node11 = new TokenSymbolNode(3, 4, 1);
		node10.addChild(node11);
		node8.addChild(node9);
		node8.addChild(node10);
		node6.addChild(node7);
		node6.addChild(node8);
		node4.addChild(node5);
		node4.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node4);
		node1.addChild(node2);
		return node1;
	}
	
}
	