package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
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
 * A ::= B A d | a
 * 
 * B ::= epsilon | b
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class IndirectRecursion2Test {

	private GrammarBuilder builder;
	private Grammar grammar;
	
	private Nonterminal A = new Nonterminal("A");
	private Nonterminal B = new Nonterminal("B");

	private Character a = new Character('a');
	private Character b = new Character('b');
	private Character d = new Character('d');

	@Before
	public void init() {
		Rule r1 = new Rule(A, list(B, A, d));
		Rule r2 = new Rule(A, list(a));
		Rule r3 = new Rule(B);
		Rule r4 = new Rule(B, list(b));

		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		builder = new GrammarBuilder("IndirectRecursion", factory).addRule(r1)
													     		  .addRule(r2)
													     		  .addRule(r3)
													     		  .addRule(r4);
		grammar = builder.build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getHeadGrammarSlot("A").isNullable());
		assertTrue(grammar.getHeadGrammarSlot("B").isNullable());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("ad");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
		assertTrue(sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testReachabilityGraph() {
		Set<HeadGrammarSlot> set = builder.getDirectReachableNonterminals("A");
		assertTrue(set.contains(grammar.getHeadGrammarSlot("A")));
		assertTrue(set.contains(grammar.getHeadGrammarSlot("B")));
	}
	
	private SPPFNode expectedSPPF() {		
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 2);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(B, A), 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalId(B), 2, 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 2, 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(grammar.getRegularExpressionId(d), 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		return node1;
	}

}