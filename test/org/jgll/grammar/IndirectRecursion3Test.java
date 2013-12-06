package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B c
 *     | C d
 *     | e
 * 
 * B ::= A f
 *     | A g
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion3Test {

	private GrammarBuilder builder;
	
	private Grammar grammar;
	private GLLParser rdParser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		
		Character c = new Character('c');
		Character d = new Character('d');
		Character e = new Character('e');
		Character f = new Character('f');
		Character g = new Character('g');
		
		Rule r1 = new Rule(A, list(B, c));
		Rule r2 = new Rule(A, list(C, d));
		Rule r3 = new Rule(A, list(e));
		Rule r4 = new Rule(B, list(A, f));
		Rule r5 = new Rule(C, list(A, g));
		
		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													     .addRule(r2)
													     .addRule(r3)
													     .addRule(r4)
													     .addRule(r5);
		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("A").isNullable());
		assertFalse(grammar.getNonterminalByName("B").isNullable());
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf1 = rdParser.parse(Input.fromString("efcfc"), grammar, "A");
		assertTrue(sppf1.deepEquals(getSPPFNode1()));
		
		NonterminalSymbolNode sppf2 = rdParser.parse(Input.fromString("egdgdgd"), grammar, "A");
		assertTrue(sppf2.deepEquals(getSPPFNode2()));
		
		NonterminalSymbolNode sppf3 = rdParser.parse(Input.fromString("egdfcgd"), grammar, "A");
		assertTrue(sppf3.deepEquals(getSPPFNode3()));
	}
	
	private SPPFNode getSPPFNode1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 4);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 2);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		TerminalSymbolNode node6 = new TerminalSymbolNode(101, 0);
		node5.addChild(node6);
		TerminalSymbolNode node7 = new TerminalSymbolNode(102, 1);
		node4.addChild(node5);
		node4.addChild(node7);
		TerminalSymbolNode node8 = new TerminalSymbolNode(99, 2);
		node3.addChild(node4);
		node3.addChild(node8);
		TerminalSymbolNode node9 = new TerminalSymbolNode(102, 3);
		node2.addChild(node3);
		node2.addChild(node9);
		TerminalSymbolNode node10 = new TerminalSymbolNode(99, 4);
		node1.addChild(node2);
		node1.addChild(node10);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 7);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 6);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		TerminalSymbolNode node8 = new TerminalSymbolNode(101, 0);
		node7.addChild(node8);
		TerminalSymbolNode node9 = new TerminalSymbolNode(103, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TerminalSymbolNode node10 = new TerminalSymbolNode(100, 2);
		node5.addChild(node6);
		node5.addChild(node10);
		TerminalSymbolNode node11 = new TerminalSymbolNode(103, 3);
		node4.addChild(node5);
		node4.addChild(node11);
		TerminalSymbolNode node12 = new TerminalSymbolNode(100, 4);
		node3.addChild(node4);
		node3.addChild(node12);
		TerminalSymbolNode node13 = new TerminalSymbolNode(103, 5);
		node2.addChild(node3);
		node2.addChild(node13);
		TerminalSymbolNode node14 = new TerminalSymbolNode(100, 6);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 7);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 6);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 0, 2);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		TerminalSymbolNode node8 = new TerminalSymbolNode(101, 0);
		node7.addChild(node8);
		TerminalSymbolNode node9 = new TerminalSymbolNode(103, 1);
		node6.addChild(node7);
		node6.addChild(node9);
		TerminalSymbolNode node10 = new TerminalSymbolNode(100, 2);
		node5.addChild(node6);
		node5.addChild(node10);
		TerminalSymbolNode node11 = new TerminalSymbolNode(102, 3);
		node4.addChild(node5);
		node4.addChild(node11);
		TerminalSymbolNode node12 = new TerminalSymbolNode(99, 4);
		node3.addChild(node4);
		node3.addChild(node12);
		TerminalSymbolNode node13 = new TerminalSymbolNode(103, 5);
		node2.addChild(node3);
		node2.addChild(node13);
		TerminalSymbolNode node14 = new TerminalSymbolNode(100, 6);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
	
}