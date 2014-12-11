package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
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
 * 
 * S ::= a S b S
 *     | a S !>> b
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar3 {

	Nonterminal S = Nonterminal.withName("S");
	Character s = Character.from('s');
	Character a = Character.from('a');
	Character b = Character.from('b');
	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = new Rule(S, list(a, S.builder().addCondition(RegularExpressionCondition.notFollow(Character.from('b'))).build()));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF()));
	}
	
	private SPPFNode getExpectedSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 5).init();
		PackedNode node2 = factory.createPackedNode("S ::= a S .", 1, node1);
		TerminalNode node3 = factory.createTokenNode("a", 0, 1);
		NonterminalNode node4 = factory.createNonterminalNode("S", 1, 5).init();
		PackedNode node5 = factory.createPackedNode("S ::= a S b S .", 4, node4);
		IntermediateNode node6 = factory.createIntermediateNode("S ::= a S b . S", 1, 4).init();
		PackedNode node7 = factory.createPackedNode("S ::= a S b . S", 3, node6);
		IntermediateNode node8 = factory.createIntermediateNode("S ::= a S . b S", 1, 3).init();
		PackedNode node9 = factory.createPackedNode("S ::= a S . b S", 2, node8);
		TerminalNode node10 = factory.createTokenNode("a", 1, 1);
		NonterminalNode node11 = factory.createNonterminalNode("S", 2, 3).init();
		PackedNode node12 = factory.createPackedNode("S ::= s .", 2, node11);
		TerminalNode node13 = factory.createTokenNode("s", 2, 1);
		node12.addChild(node13);
		node11.addChild(node12);
		node9.addChild(node10);
		node9.addChild(node11);
		node8.addChild(node9);
		TerminalNode node14 = factory.createTokenNode("b", 3, 1);
		node7.addChild(node8);
		node7.addChild(node14);
		node6.addChild(node7);
		NonterminalNode node15 = factory.createNonterminalNode("S", 4, 5).init();
		PackedNode node16 = factory.createPackedNode("S ::= s .", 4, node15);
		TerminalNode node17 = factory.createTokenNode("s", 4, 1);
		node16.addChild(node17);
		node15.addChild(node16);
		node5.addChild(node6);
		node5.addChild(node15);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		node1.addChild(node2);
		return node1;
	}

}
