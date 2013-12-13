package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.condition.ConditionFactory;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
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
 * S ::= a S b S
 *     | a S \ a S b S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar2 {

	private Grammar grammar;
	
	private GLLParser parser;

	@Before
	public void init() {
		
		GrammarBuilder builder = new GrammarBuilder("DanglingElse");
		
		Nonterminal S = new Nonterminal("S");
		Terminal s = new Character('s');
		Terminal a = new Character('a');
		Terminal b = new Character('b');

		Rule rule1 = new Rule(S, list(Group.of(a, S).addCondition(ConditionFactory.notMatch(a, S, b, S))));
		builder.addRules(EBNFUtil.rewrite(rule1));
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = parser.parse(Input.fromString("aasbs"), grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF()));
	}
	
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S [b] . S"), 0, 4);
		IntermediateNode node3 = new IntermediateNode(grammar.getGrammarSlotByName("S ::= [a] S . [b] S"), 0, 3);
		TokenSymbolNode node4 = new TokenSymbolNode(2, 0, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 1, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalByName("([a] S)"), 1, 3);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(4, 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		TokenSymbolNode node10 = new TokenSymbolNode(3, 3, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 4, 5);
		TokenSymbolNode node12 = new TokenSymbolNode(4, 4, 1);
		node11.addChild(node12);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}

}
