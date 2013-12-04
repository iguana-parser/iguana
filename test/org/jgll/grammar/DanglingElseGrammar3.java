package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.condition.ConditionFactory;
import org.jgll.grammar.symbol.Character;
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
 * 
 * S ::= a S b S
 *     | a S !>> b
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar3 {

	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("DanglingElse");
		
		Nonterminal S = new Nonterminal("S");
		Terminal s = new Character('s');
		Terminal a = new Character('a');
		Terminal b = new Character('b');

		Rule rule1 = new Rule(S, list(a, S.addCondition(ConditionFactory.notFollow(b, S))));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar =  builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("aasbs"), grammar, "S");
		assertEquals(true, sppf.deepEquals(getExpectedSPPF()));
	}
	
	
	private SPPFNode getExpectedSPPF() {
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

}
