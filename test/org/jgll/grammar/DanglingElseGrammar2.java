package org.jgll.grammar;

import static org.jgll.util.collections.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.condition.ConditionFactory;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.parser.ParseError;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * S ::= a S b S
 *     | a S \ a S b S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar2 extends AbstractGrammarTest {

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

		rule1 = new Rule(S, list(Group.of(a, S).addCondition(ConditionFactory.notMatch(a, S, b, S))));
		builder.addRules(EBNFUtil.rewrite(rule1));
		
		rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);
		
		rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		return builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("aasbs"), grammar, "S");
		assertEquals(true, sppf.deepEquals(getExpectedSPPF()));
	}
	
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 0, 4);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 0, 3);
		TerminalSymbolNode node4 = new TerminalSymbolNode(97, 0);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S )"), 1, 3);
		TerminalSymbolNode node7 = new TerminalSymbolNode(97, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TerminalSymbolNode node9 = new TerminalSymbolNode(115, 2);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		TerminalSymbolNode node10 = new TerminalSymbolNode(98, 3);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5);
		TerminalSymbolNode node12 = new TerminalSymbolNode(115, 4);
		node11.addChild(node12);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}

}
