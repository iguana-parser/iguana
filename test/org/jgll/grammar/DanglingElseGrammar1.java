package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
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
 * S ::= a S b S \ a S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar1 {

	private Grammar grammar;
	private GLLParser parser;

	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("DanglingElse", factory);
		
		Nonterminal S = new Nonterminal("S");
		Character s = new Character('s');
		Character a = new Character('a');
		Character b = new Character('b');

		Rule rule1 = new Rule(S, list(a, S));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(Group.of(a, S, b, S).addCondition(ContextFreeCondition.notMatch(a, S))));
		
		builder.addRules(EBNFUtil.rewrite(rule2));
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("aasbs");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF1()));
	}
	
	@Test
	public void test2() throws ParseError {
		Input input = Input.fromString("aaaaasbsbsbs");
		parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("aaaaasbsbsbs"), grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF2()));
	}
	
	private SPPFNode getExpectedSPPF1() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 0, 5);
		TokenSymbolNode node2 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 1, 5);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("([a] S [b] S)"), 1, 5);
		IntermediateNode node5 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S [b] . S"), 1, 4);
		IntermediateNode node6 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S . [b] S"), 1, 3);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(4, 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		TokenSymbolNode node10 = new TokenSymbolNode(3, 3, 1);
		node5.addChild(node6);
		node5.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 4, 5);
		TokenSymbolNode node12 = new TokenSymbolNode(4, 4, 1);
		node11.addChild(node12);
		node4.addChild(node5);
		node4.addChild(node11);
		node3.addChild(node4);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}
	
	private SPPFNode getExpectedSPPF2() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 0, 12);
		TokenSymbolNode node2 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 1, 12);
		TokenSymbolNode node4 = new TokenSymbolNode(2, 1, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 2, 12);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("([a] S [b] S)"), 2, 12);
		IntermediateNode node7 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S [b] . S"), 2, 11);
		IntermediateNode node8 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S . [b] S"), 2, 10);
		TokenSymbolNode node9 = new TokenSymbolNode(2, 2, 1);
		NonterminalSymbolNode node10 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 3, 10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("([a] S [b] S)"), 3, 10);
		IntermediateNode node12 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S [b] . S"), 3, 9);
		IntermediateNode node13 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S . [b] S"), 3, 8);
		TokenSymbolNode node14 = new TokenSymbolNode(2, 3, 1);
		NonterminalSymbolNode node15 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 4, 8);
		NonterminalSymbolNode node16 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("([a] S [b] S)"), 4, 8);
		IntermediateNode node17 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S [b] . S"), 4, 7);
		IntermediateNode node18 = new IntermediateNode(grammar.getGrammarSlotByName("([a] S [b] S) ::= [a] S . [b] S"), 4, 6);
		TokenSymbolNode node19 = new TokenSymbolNode(2, 4, 1);
		NonterminalSymbolNode node20 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 5, 6);
		TokenSymbolNode node21 = new TokenSymbolNode(4, 5, 1);
		node20.addChild(node21);
		node18.addChild(node19);
		node18.addChild(node20);
		TokenSymbolNode node22 = new TokenSymbolNode(3, 6, 1);
		node17.addChild(node18);
		node17.addChild(node22);
		NonterminalSymbolNode node23 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 7, 8);
		TokenSymbolNode node24 = new TokenSymbolNode(4, 7, 1);
		node23.addChild(node24);
		node16.addChild(node17);
		node16.addChild(node23);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		TokenSymbolNode node25 = new TokenSymbolNode(3, 8, 1);
		node12.addChild(node13);
		node12.addChild(node25);
		NonterminalSymbolNode node26 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 9, 10);
		TokenSymbolNode node27 = new TokenSymbolNode(4, 9, 1);
		node26.addChild(node27);
		node11.addChild(node12);
		node11.addChild(node26);
		node10.addChild(node11);
		node8.addChild(node9);
		node8.addChild(node10);
		TokenSymbolNode node28 = new TokenSymbolNode(3, 10, 1);
		node7.addChild(node8);
		node7.addChild(node28);
		NonterminalSymbolNode node29 = new NonterminalSymbolNode(grammar.getHeadGrammarSlot("S"), 11, 12);
		TokenSymbolNode node30 = new TokenSymbolNode(4, 11, 1);
		node29.addChild(node30);
		node6.addChild(node7);
		node6.addChild(node29);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		node1.addChild(node2);
		node1.addChild(node3);
		return node1;
	}

}
