package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
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
 * S ::= a S b S
 *     | a S \ a S b S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar2 {

	private Grammar grammar;
	
	private Nonterminal S = new Nonterminal("S");
	private Character s = new Character('s');
	private Character a = new Character('a');
	private Character b = new Character('b');
	private Group group = Group.of(a, S);

	
	@Before
	public void createGrammar() {
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder("DanglingElse", factory);
		
		Rule rule1 = new Rule(S, list(group.withCondition(ContextFreeCondition.notMatch(a, S, b, S))));
		builder.addRules(EBNFUtil.rewrite(rule1));
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF()));
	}
	
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 0, 5);
		IntermediateNode node2 = new IntermediateNode(grammar.getIntermediateNodeId(a, S, b), 0, 4);
		IntermediateNode node3 = new IntermediateNode(grammar.getIntermediateNodeId(a, S), 0, 3);
		TokenSymbolNode node4 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 1, 3);
		NonterminalSymbolNode node6 = new NonterminalSymbolNode(grammar.getNonterminalId(group), 1, 1, 3);
		TokenSymbolNode node7 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 1, 1);
		NonterminalSymbolNode node8 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 2, 3);
		TokenSymbolNode node9 = new TokenSymbolNode(grammar.getRegularExpressionId(s), 2, 1);
		node8.addChild(node9);
		node6.addChild(node7);
		node6.addChild(node8);
		node5.addChild(node6);
		node3.addChild(node4);
		node3.addChild(node5);
		TokenSymbolNode node10 = new TokenSymbolNode(grammar.getRegularExpressionId(b), 3, 1);
		node2.addChild(node3);
		node2.addChild(node10);
		NonterminalSymbolNode node11 = new NonterminalSymbolNode(grammar.getNonterminalId(S), 3, 4, 5);
		TokenSymbolNode node12 = new TokenSymbolNode(grammar.getRegularExpressionId(s), 4, 1);
		node11.addChild(node12);
		node1.addChild(node2);
		node1.addChild(node11);
		return node1;
	}

}
