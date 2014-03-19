package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Test8 {
	
	private Grammar grammar;
	
	private Nonterminal A = new Nonterminal("A");

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(A);

		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("IndirectRecursion", factory)
													  .addRule(r1)
													  .addRule(r2)
													  .addRule(r3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
//		assertTrue(sppf.deepEquals(getSPPF1()));
	}
	
	@Test
	public void test2() throws ParseError {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
	}

	
	private SPPFNode getSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 3, 0, 0);
		PackedNode node2 = new PackedNode(grammar.getPackedNodeId(A, new ArrayList<Symbol>()), 0, node1);
		PackedNode node3 = new PackedNode(grammar.getPackedNodeId(A, A, A), 0, node1);
		node3.addChild(node1);
		node3.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}
}
