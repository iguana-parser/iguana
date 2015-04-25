package org.iguana.parser;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= a S b
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar4 {

	Nonterminal S = Nonterminal.withName("S");
	Character s = Character.from('s');
	Character a = Character.from('a');
	Character b = Character.from('b');
	private Grammar grammar;

	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = Rule.withHead(S).addSymbols(a, S, b).build();
		builder.addRule(rule1);
		
		Rule rule2 = Rule.withHead(S).addSymbols(a, S).build();
		builder.addRule(rule2);
		
		Rule rule3 = Rule.withHead(S).addSymbols(s).build();
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("aasb");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getGrammarGraph())));
	}
	
	private SPPFNode getExpectedSPPF(GrammarGraph registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 4);
		PackedNode node2 = factory.createPackedNode("S ::= a S b .", 3, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= a S . b", 0, 3);
		PackedNode node4 = factory.createPackedNode("S ::= a S . b", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 3);
		PackedNode node7 = factory.createPackedNode("S ::= a S .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
		NonterminalNode node9 = factory.createNonterminalNode("S", 0, 2, 3);
		PackedNode node10 = factory.createPackedNode("S ::= s .", 3, node9);
		TerminalNode node11 = factory.createTerminalNode("s", 2, 3);
		node10.addChild(node11);
		node9.addChild(node10);
		node7.addChild(node8);
		node7.addChild(node9);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node12 = factory.createTerminalNode("b", 3, 4);
		node2.addChild(node3);
		node2.addChild(node12);
		PackedNode node13 = factory.createPackedNode("S ::= a S .", 1, node1);
		NonterminalNode node15 = factory.createNonterminalNode("S", 0, 1, 4);
		PackedNode node16 = factory.createPackedNode("S ::= a S b .", 3, node15);
		IntermediateNode node17 = factory.createIntermediateNode("S ::= a S . b", 1, 3);
		PackedNode node18 = factory.createPackedNode("S ::= a S . b", 2, node17);
		node18.addChild(node8);
		node18.addChild(node9);
		node17.addChild(node18);
		node16.addChild(node17);
		node16.addChild(node12);
		node15.addChild(node16);
		node13.addChild(node5);
		node13.addChild(node15);
		node1.addChild(node2);
		node1.addChild(node13);
		return node1;
	}

}
