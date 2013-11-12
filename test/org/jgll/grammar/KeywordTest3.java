package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
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
	private GLLParser rdParser;
	private GLLParser levelParser;

	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Keyword iff = new Keyword("if", new int[] {'i', 'f'});
		Keyword then = new Keyword("then", new int[] {'t', 'h', 'e', 'n'});
		Nonterminal L = new Nonterminal("L");
		Terminal s = new Character('s');
		Terminal ws = new Character(' ');
		
		Rule r1 = new Rule(S, iff, L, S, L, then, L, S);
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		
		grammar = new GrammarBuilder().addRule(r1)
								   .addRule(r2)
								   .addRule(r3)
								   .addRule(GrammarBuilder.fromKeyword(iff))
								   .addRule(GrammarBuilder.fromKeyword(then)).build();
		
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
		levelParser = ParserFactory.createLevelParser(grammar);
	}
	
	
	@Test
	public void testFirstSet() {
		assertEquals(set(new Character('i'), new Character('s')), grammar.getNonterminalByName("S").getFirstSet());
	}
	
	@Test
	public void testKeywordLength() {
		assertEquals(4, grammar.getLongestTerminalChain());
	}

	@Test
	public void testRDParser() throws ParseError {
		Input input = Input.fromString("if s then s");
		NonterminalSymbolNode sppf = rdParser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void testLevelParser() throws ParseError {
		Input input = Input.fromString("if s then s");
		NonterminalSymbolNode sppf = levelParser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getSPPF2()));
	}
	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 11);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("if"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 2, 3);
		TerminalSymbolNode node4 = new TerminalSymbolNode(32, 2);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 4);
		TerminalSymbolNode node6 = new TerminalSymbolNode(115, 3);
		node5.addChild(node6);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 4, 5);
		TerminalSymbolNode node8 = new TerminalSymbolNode(32, 4);
		node7.addChild(node8);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("then"), 5, 9);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 9, 10);
		TerminalSymbolNode node11 = new TerminalSymbolNode(32, 9);
		node10.addChild(node11);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 10, 11);
		TerminalSymbolNode node13 = new TerminalSymbolNode(115, 10);
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
	
	private SPPFNode getSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 11);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= if L S L then L . S"), 0, 10);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= if L S L then . L S"), 0, 9);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= if L S L . then L S"), 0, 5);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= if L S . L then L S"), 0, 4);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= if L . S L then L S"), 0, 3);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("if"), 0, 2);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(32, 2);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 4);
		TerminalSymbolNode node11 = new TerminalSymbolNode(115, 3);
		node10.addChild(node11);
		node5.addChild(node6);
		node5.addChild(node10);
		NonterminalSymbolNode node12 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 4, 5);
		TerminalSymbolNode node13 = new TerminalSymbolNode(32, 4);
		node12.addChild(node13);
		node4.addChild(node5);
		node4.addChild(node12);
		NonterminalSymbolNode node14 = new NonterminalSymbolNode(grammar.getNonterminalByName("then"), 5, 9);
		node3.addChild(node4);
		node3.addChild(node14);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getNonterminalByName("L"), 9, 10);
		TerminalSymbolNode node16 = new TerminalSymbolNode(32, 9);
		node15.addChild(node16);
		node2.addChild(node3);
		node2.addChild(node15);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 10, 11);
		TerminalSymbolNode node18 = new TerminalSymbolNode(115, 10);
		node17.addChild(node18);
		node1.addChild(node2);
		node1.addChild(node17);
		return node1;
	}
	
}
