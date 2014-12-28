package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
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

/**
 * 
 * S ::= a S b S 
 * 	   | a S \ a S b S 
 *     | s
 * 
 * @author Ali Afroozeh
 * 
 */
// TODO: context-free conditions don't work
public class DanglingElseGrammar2 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Character s = Character.from('s');
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Group group = Group.builder(a, S).addPreCondition(ContextFreeCondition.notMatch(a, S, b, S)).build();

	@Before
	public void createGrammar() {

		Grammar.Builder builder = new Grammar.Builder();

		Rule rule1 = new Rule(S, list(group));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);

		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}

	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}

	private SPPFNode getExpectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 5).init();
		PackedNode node2 = factory.createPackedNode("S ::= (a S) .", 0, node1);
		NonterminalNode node3 = factory.createNonterminalNode("(a S)", 0, 0, 5).init();
		PackedNode node4 = factory.createPackedNode("(a S) ::= a S .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("S", 0, 1, 5).init();
		PackedNode node7 = factory.createPackedNode("S ::= a S b S .", 4, node6);
		IntermediateNode node8 = factory.createIntermediateNode("S ::= a S b . S", 1, 4).init();
		PackedNode node9 = factory.createPackedNode("S ::= a S b . S", 3, node8);
		IntermediateNode node10 = factory.createIntermediateNode("S ::= a S . b S", 1, 3).init();
		PackedNode node11 = factory.createPackedNode("S ::= a S . b S", 2, node10);
		TerminalNode node12 = factory.createTerminalNode("a", 1, 1);
		NonterminalNode node13 = factory.createNonterminalNode("S", 0, 2, 3).init();
		PackedNode node14 = factory.createPackedNode("S ::= s .", 2, node13);
		TerminalNode node15 = factory.createTerminalNode("s", 2, 1);
		node14.addChild(node15);
		node13.addChild(node14);
		node11.addChild(node12);
		node11.addChild(node13);
		node10.addChild(node11);
		TerminalNode node16 = factory.createTerminalNode("b", 3, 1);
		node9.addChild(node10);
		node9.addChild(node16);
		node8.addChild(node9);
		NonterminalNode node17 = factory.createNonterminalNode("S", 0, 4, 5).init();
		PackedNode node18 = factory.createPackedNode("S ::= s .", 4, node17);
		TerminalNode node19 = factory.createTerminalNode("s", 4, 1);
		node18.addChild(node19);
		node17.addChild(node18);
		node7.addChild(node8);
		node7.addChild(node17);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		node2.addChild(node3);
		PackedNode node20 = factory.createPackedNode("S ::= a S b S .", 4, node1);
		IntermediateNode node21 = factory.createIntermediateNode("S ::= a S b . S", 0, 4).init();
		PackedNode node22 = factory.createPackedNode("S ::= a S b . S", 3, node21);
		IntermediateNode node23 = factory.createIntermediateNode("S ::= a S . b S", 0, 3).init();
		PackedNode node24 = factory.createPackedNode("S ::= a S . b S", 1, node23);
		NonterminalNode node25 = factory.createNonterminalNode("S", 0, 1, 3).init();
		PackedNode node26 = factory.createPackedNode("S ::= (a S) .", 1, node25);
		NonterminalNode node27 = factory.createNonterminalNode("(a S)", 0, 1, 3).init();
		PackedNode node28 = factory.createPackedNode("(a S) ::= a S .", 2, node27);
		node28.addChild(node12);
		node28.addChild(node13);
		node27.addChild(node28);
		node26.addChild(node27);
		node25.addChild(node26);
		node24.addChild(node5);
		node24.addChild(node25);
		node23.addChild(node24);
		node22.addChild(node23);
		node22.addChild(node16);
		node21.addChild(node22);
		node20.addChild(node21);
		node20.addChild(node17);
		node1.addChild(node2);
		node1.addChild(node20);
		return node1;
	}

}
