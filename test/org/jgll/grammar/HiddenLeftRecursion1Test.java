package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

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
 * A ::= B A a
 *     | D A b
 *     | c
 * 
 * B ::= x | epsilon
 * 
 * D ::= y | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class HiddenLeftRecursion1Test {

	private GrammarBuilder builder;
	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Nonterminal D = new Nonterminal("D");
		
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		Character x = new Character('x');
		Character y = new Character('y');
		
		Rule r1 = new Rule(A, list(B, A, a));
		Rule r2 = new Rule(A, list(D, A, b));
		Rule r3 = new Rule(A, list(c));
		Rule r4 = new Rule(B, list(x));
		Rule r5 = new Rule(B);
		Rule r6 = new Rule(D, list(y));
		Rule r7 = new Rule(D);
		

		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
														 .addRule(r2)
														 .addRule(r3)
														 .addRule(r4)
														 .addRule(r5)
														 .addRule(r6)
														 .addRule(r7);
			
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf1 = parser.parse(Input.fromString("xca"), grammar, "A");
		assertTrue(sppf1.deepEquals(getSPPFNode1()));
		
		NonterminalSymbolNode sppf2 = parser.parse(Input.fromString("ycb"), grammar, "A");
		assertTrue(sppf2.deepEquals(getSPPFNode2()));
		
		NonterminalSymbolNode sppf3 = parser.parse(Input.fromString("cababaab"), grammar, "A");
		assertTrue(sppf3.deepEquals(getSPPFNode3()));
		
		NonterminalSymbolNode sppf4 = parser.parse(Input.fromString("xcabbbbb"), grammar, "A");
		assertTrue(sppf4.deepEquals(getSPPFNode4()));

		NonterminalSymbolNode sppf5 = parser.parse(Input.fromString("ycaaaabaaaa"), grammar, "A");
		assertTrue(sppf5.deepEquals(getSPPFNode5()));
	}
	
	private SPPFNode getSPPFNode1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 2);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [a]"), 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(5, 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(4, 1, 1);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		TokenSymbolNode node8 = new TokenSymbolNode(2, 2, 1);
		node1.addChild(node2);
		node1.addChild(node8);
		return node1;
	}
	
	private SPPFNode getSPPFNode2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 2);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D . A [b]"), 0, 1);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(6, 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TokenSymbolNode node7 = new TokenSymbolNode(4, 1, 1);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		TokenSymbolNode node8 = new TokenSymbolNode(3, 2, 1);
		node1.addChild(node2);
		node1.addChild(node8);
		return node1;
	}
	
	private SPPFNode getSPPFNode3() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 8);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 7);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D . A [b]"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 0);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 7);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 6);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [a]"), 0, 0);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 6);
		IntermediateNode node10 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 5);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 4);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 4);
		IntermediateNode node14 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 3);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		IntermediateNode node16 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 2);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		IntermediateNode node18 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 1);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		TokenSymbolNode node20 = new TokenSymbolNode(4, 0, 1);
		node19.addChild(node20);
		node18.addChild(node7);
		node18.addChild(node19);
		TokenSymbolNode node21 = new TokenSymbolNode(2, 1, 1);
		node17.addChild(node18);
		node17.addChild(node21);
		node16.addChild(node3);
		node16.addChild(node17);
		TokenSymbolNode node22 = new TokenSymbolNode(3, 2, 1);
		node15.addChild(node16);
		node15.addChild(node22);
		node14.addChild(node7);
		node14.addChild(node15);
		TokenSymbolNode node23 = new TokenSymbolNode(2, 3, 1);
		node13.addChild(node14);
		node13.addChild(node23);
		node12.addChild(node3);
		node12.addChild(node13);
		TokenSymbolNode node24 = new TokenSymbolNode(3, 4, 1);
		node11.addChild(node12);
		node11.addChild(node24);
		node10.addChild(node7);
		node10.addChild(node11);
		TokenSymbolNode node25 = new TokenSymbolNode(2, 5, 1);
		node9.addChild(node10);
		node9.addChild(node25);
		node6.addChild(node7);
		node6.addChild(node9);
		TokenSymbolNode node26 = new TokenSymbolNode(2, 6, 1);
		node5.addChild(node6);
		node5.addChild(node26);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node27 = new TokenSymbolNode(3, 7, 1);
		node1.addChild(node2);
		node1.addChild(node27);
		return node1;
	}
	
	private SPPFNode getSPPFNode4() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 8);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 7);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D . A [b]"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 0);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 7);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 6);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 6);
		IntermediateNode node8 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 5);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 5);
		IntermediateNode node10 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 4);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 4);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 3);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 3);
		IntermediateNode node14 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 2);
		IntermediateNode node15 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [a]"), 0, 1);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TokenSymbolNode node17 = new TokenSymbolNode(5, 0, 1);
		node16.addChild(node17);
		node15.addChild(node16);
		NonterminalSymbolNode node18 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TokenSymbolNode node19 = new TokenSymbolNode(4, 1, 1);
		node18.addChild(node19);
		node14.addChild(node15);
		node14.addChild(node18);
		TokenSymbolNode node20 = new TokenSymbolNode(2, 2, 1);
		node13.addChild(node14);
		node13.addChild(node20);
		node12.addChild(node3);
		node12.addChild(node13);
		TokenSymbolNode node21 = new TokenSymbolNode(3, 3, 1);
		node11.addChild(node12);
		node11.addChild(node21);
		node10.addChild(node3);
		node10.addChild(node11);
		TokenSymbolNode node22 = new TokenSymbolNode(3, 4, 1);
		node9.addChild(node10);
		node9.addChild(node22);
		node8.addChild(node3);
		node8.addChild(node9);
		TokenSymbolNode node23 = new TokenSymbolNode(3, 5, 1);
		node7.addChild(node8);
		node7.addChild(node23);
		node6.addChild(node3);
		node6.addChild(node7);
		TokenSymbolNode node24 = new TokenSymbolNode(3, 6, 1);
		node5.addChild(node6);
		node5.addChild(node24);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node25 = new TokenSymbolNode(3, 7, 1);
		node1.addChild(node2);
		node1.addChild(node25);
		return node1;
	}
	
	private SPPFNode getSPPFNode5() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 11);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 10);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [a]"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 10);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 9);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 9);
		IntermediateNode node8 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 8);
		IntermediateNode node10 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 0, 7);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 7);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D A . [b]"), 0, 6);
		IntermediateNode node13 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= D . A [b]"), 0, 1);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 1);
		TokenSymbolNode node15 = new TokenSymbolNode(6, 0, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 6);
		IntermediateNode node17 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 1, 5);
		IntermediateNode node18 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [a]"), 1, 1);
		NonterminalSymbolNode node19 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 1, 1);
		node18.addChild(node19);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 5);
		IntermediateNode node21 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 1, 4);
		NonterminalSymbolNode node22 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 4);
		IntermediateNode node23 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 1, 3);
		NonterminalSymbolNode node24 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 3);
		IntermediateNode node25 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [a]"), 1, 2);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 1, 2);
		TokenSymbolNode node27 = new TokenSymbolNode(4, 1, 1);
		node26.addChild(node27);
		node25.addChild(node18);
		node25.addChild(node26);
		TokenSymbolNode node28 = new TokenSymbolNode(2, 2, 1);
		node24.addChild(node25);
		node24.addChild(node28);
		node23.addChild(node18);
		node23.addChild(node24);
		TokenSymbolNode node29 = new TokenSymbolNode(2, 3, 1);
		node22.addChild(node23);
		node22.addChild(node29);
		node21.addChild(node18);
		node21.addChild(node22);
		TokenSymbolNode node30 = new TokenSymbolNode(2, 4, 1);
		node20.addChild(node21);
		node20.addChild(node30);
		node17.addChild(node18);
		node17.addChild(node20);
		TokenSymbolNode node31 = new TokenSymbolNode(2, 5, 1);
		node16.addChild(node17);
		node16.addChild(node31);
		node12.addChild(node13);
		node12.addChild(node16);
		TokenSymbolNode node32 = new TokenSymbolNode(3, 6, 1);
		node11.addChild(node12);
		node11.addChild(node32);
		node10.addChild(node3);
		node10.addChild(node11);
		TokenSymbolNode node33 = new TokenSymbolNode(2, 7, 1);
		node9.addChild(node10);
		node9.addChild(node33);
		node8.addChild(node3);
		node8.addChild(node9);
		TokenSymbolNode node34 = new TokenSymbolNode(2, 8, 1);
		node7.addChild(node8);
		node7.addChild(node34);
		node6.addChild(node3);
		node6.addChild(node7);
		TokenSymbolNode node35 = new TokenSymbolNode(2, 9, 1);
		node5.addChild(node6);
		node5.addChild(node35);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node36 = new TokenSymbolNode(2, 10, 1);
		node1.addChild(node2);
		node1.addChild(node36);
		return node1;
	}
	
}