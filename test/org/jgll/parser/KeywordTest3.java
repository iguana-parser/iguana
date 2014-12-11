package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
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

	private Nonterminal S = Nonterminal.withName("S");
	private Keyword iff = Keyword.from("if");
	private Keyword then = Keyword.from("then");
	private Nonterminal L = Nonterminal.withName("L");
	private Character s = Character.from('s');
	private Character ws = Character.from(' ');

	@Before
	public void init() {
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff, s), grammar.getFirstSet(S));
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("if s then s");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getSPPF()));
	}
		
	private SPPFNode getSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 11).init();
		PackedNode node2 = factory.createPackedNode("S ::= i f L S L t h e n L S .", 10, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= i f L S L t h e n L . S", 0, 10).init();
		PackedNode node4 = factory.createPackedNode("S ::= i f L S L t h e n L . S", 9, node3);
		IntermediateNode node5 = factory.createIntermediateNode("S ::= i f L S L t h e n . L S", 0, 9).init();
		PackedNode node6 = factory.createPackedNode("S ::= i f L S L t h e n . L S", 5, node5);
		IntermediateNode node7 = factory.createIntermediateNode("S ::= i f L S L . t h e n L S", 0, 5).init();
		PackedNode node8 = factory.createPackedNode("S ::= i f L S L . t h e n L S", 4, node7);
		IntermediateNode node9 = factory.createIntermediateNode("S ::= i f L S . L t h e n L S", 0, 4).init();
		PackedNode node10 = factory.createPackedNode("S ::= i f L S . L t h e n L S", 3, node9);
		IntermediateNode node11 = factory.createIntermediateNode("S ::= i f L . S L t h e n L S", 0, 3).init();
		PackedNode node12 = factory.createPackedNode("S ::= i f L . S L t h e n L S", 2, node11);
		TerminalNode node13 = factory.createTokenNode("i f", 0, 2);
		NonterminalNode node14 = factory.createNonterminalNode("L", 2, 3).init();
		PackedNode node15 = factory.createPackedNode("L ::= \\u0020 .", 2, node14);
		TerminalNode node16 = factory.createTokenNode("\\u0020", 2, 1);
		node15.addChild(node16);
		node14.addChild(node15);
		node12.addChild(node13);
		node12.addChild(node14);
		node11.addChild(node12);
		NonterminalNode node17 = factory.createNonterminalNode("S", 3, 4).init();
		PackedNode node18 = factory.createPackedNode("S ::= s .", 3, node17);
		TerminalNode node19 = factory.createTokenNode("s", 3, 1);
		node18.addChild(node19);
		node17.addChild(node18);
		node10.addChild(node11);
		node10.addChild(node17);
		node9.addChild(node10);
		NonterminalNode node20 = factory.createNonterminalNode("L", 4, 5).init();
		PackedNode node21 = factory.createPackedNode("L ::= \\u0020 .", 4, node20);
		TerminalNode node22 = factory.createTokenNode("\\u0020", 4, 1);
		node21.addChild(node22);
		node20.addChild(node21);
		node8.addChild(node9);
		node8.addChild(node20);
		node7.addChild(node8);
		TerminalNode node23 = factory.createTokenNode("t h e n", 5, 4);
		node6.addChild(node7);
		node6.addChild(node23);
		node5.addChild(node6);
		NonterminalNode node24 = factory.createNonterminalNode("L", 9, 10).init();
		PackedNode node25 = factory.createPackedNode("L ::= \\u0020 .", 9, node24);
		TerminalNode node26 = factory.createTokenNode("\\u0020", 9, 1);
		node25.addChild(node26);
		node24.addChild(node25);
		node4.addChild(node5);
		node4.addChild(node24);
		node3.addChild(node4);
		NonterminalNode node27 = factory.createNonterminalNode("S", 10, 11).init();
		PackedNode node28 = factory.createPackedNode("S ::= s .", 10, node27);
		TerminalNode node29 = factory.createTokenNode("s", 10, 1);
		node28.addChild(node29);
		node27.addChild(node28);
		node2.addChild(node3);
		node2.addChild(node27);
		node1.addChild(node2);
		return node1;
	}
	
}
