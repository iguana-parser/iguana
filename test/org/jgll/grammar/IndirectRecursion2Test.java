package org.jgll.grammar;

import static org.junit.Assert.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import java.util.Set;

import org.jgll.parser.ParseError;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
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
public class IndirectRecursion2Test extends AbstractGrammarTest {

	private GrammarBuilder builder;

	@Override
	protected Grammar initGrammar() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Rule r1 = new Rule(A, list(B, A, new Character('d')));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(B);
		Rule r4 = new Rule(B, list(new Character('b')));
		builder = new GrammarBuilder("IndirectRecursion").addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4);
		return builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("ad"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	@Test
	public void testReachabilityGraph() {
		Set<HeadGrammarSlot> set = builder.getReachableNonterminals("A");
		assertEquals(true, set.contains(grammar.getNonterminalByName("A")));
		assertEquals(true, set.contains(grammar.getNonterminalByName("B")));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B A . [d]"), 0, 1);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("A ::= B . A [d]"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0);
		TerminalSymbolNode node5 = new TerminalSymbolNode(-2, 0);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		TerminalSymbolNode node7 = new TerminalSymbolNode(97, 0);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		TerminalSymbolNode node8 = new TerminalSymbolNode(100, 1);
		node1.addChild(node2);
		node1.addChild(node8);
		return node1;
	}

}