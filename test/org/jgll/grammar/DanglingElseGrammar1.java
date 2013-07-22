package org.jgll.grammar;

import org.jgll.grammar.condition.ConditionFactory;
import org.jgll.parser.ParseError;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * S ::= a S b S \ a S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar1 extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("DanglingElse");
		
		Nonterminal S = new Nonterminal("S");
		Terminal s = new Character('s');
		Terminal a = new Character('a');
		Terminal b = new Character('b');

		rule1 = new Rule(S, list(a, S));
		builder.addRule(rule1);
		
		rule2 = new Rule(S, list(a, S, b, S)).addCondition(ConditionFactory.notMatch(list(a, S)));
		builder.addRule(rule2);
		
		rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		return builder.build();
	}
	
	@Test
	public void test1() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("aasbs"), grammar, "S");
		assertEquals(true, sppf.deepEquals(getExpectedSPPF1()));
	}
	
	@Test
	public void test2() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("aaaaasbsbsbs"), grammar, "S");
		assertEquals(true, sppf.deepEquals(getExpectedSPPF2()));
	}
	
	
	private SPPFNode getExpectedSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5);
		TerminalSymbolNode node2 = new TerminalSymbolNode(97, 0);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 5);
		IntermediateNode node4 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 1, 4);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 1, 3);
		TerminalSymbolNode node6 = new TerminalSymbolNode(97, 1);
		NonterminalSymbolNode node7 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TerminalSymbolNode node8 = new TerminalSymbolNode(115, 2);
		node7.addChild(node8);
		node5.addChild(node6);
		node5.addChild(node7);
		TerminalSymbolNode node9 = new TerminalSymbolNode(98, 3);
		node4.addChild(node5);
		node4.addChild(node9);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5);
		TerminalSymbolNode node11 = new TerminalSymbolNode(115, 4);
		node10.addChild(node11);
		node3.addChild(node4);
		node3.addChild(node10);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}
	
	private SPPFNode getExpectedSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 12);
		TerminalSymbolNode node2 = new TerminalSymbolNode(97, 0);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 12);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 12);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 2, 11);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 2, 10);
		TerminalSymbolNode node8 = new TerminalSymbolNode(97, 2);
		NonterminalSymbolNode node9 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 3, 10);
		IntermediateNode node10 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 3, 9);
		IntermediateNode node11 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 3, 8);
		TerminalSymbolNode node12 = new TerminalSymbolNode(97, 3);
		NonterminalSymbolNode node13 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 8);
		IntermediateNode node14 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 4, 7);
		IntermediateNode node15 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 4, 6);
		TerminalSymbolNode node16 = new TerminalSymbolNode(97, 4);
		NonterminalSymbolNode node17 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 5, 6);
		TerminalSymbolNode node18 = new TerminalSymbolNode(115, 5);
		node17.addChild(node18);
		node15.addChild(node16);
		node15.addChild(node17);
		TerminalSymbolNode node19 = new TerminalSymbolNode(98, 6);
		node14.addChild(node15);
		node14.addChild(node19);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 7, 8);
		TerminalSymbolNode node21 = new TerminalSymbolNode(115, 7);
		node20.addChild(node21);
		node13.addChild(node14);
		node13.addChild(node20);
		node11.addChild(node12);
		node11.addChild(node13);
		TerminalSymbolNode node22 = new TerminalSymbolNode(98, 8);
		node10.addChild(node11);
		node10.addChild(node22);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 9, 10);
		TerminalSymbolNode node24 = new TerminalSymbolNode(115, 9);
		node23.addChild(node24);
		node9.addChild(node10);
		node9.addChild(node23);
		node7.addChild(node8);
		node7.addChild(node9);
		TerminalSymbolNode node25 = new TerminalSymbolNode(98, 10);
		node6.addChild(node7);
		node6.addChild(node25);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 11, 12);
		TerminalSymbolNode node27 = new TerminalSymbolNode(115, 11);
		node26.addChild(node27);
		node5.addChild(node6);
		node5.addChild(node26);
		node3.addChild(node4);
		node3.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}

}
