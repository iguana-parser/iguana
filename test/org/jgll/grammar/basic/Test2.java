package org.jgll.grammar.basic;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 */
public class Test2 {

	private Grammar grammar;

	private Nonterminal A = new Nonterminal("A");
	private Character a = new Character('a');
	
	@Before
	public void init() {
		Rule r1 = new Rule(A, list(a));
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		grammar = new GrammarBuilder("a", factory).addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getHeadGrammarSlot("A").isNullable());
	}
	
	@Test
	public void testParser() throws ParseError {
		Input input = Input.fromString("a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalId(A), 1, 0, 1);
		TokenSymbolNode node2 = new TokenSymbolNode(grammar.getRegularExpressionId(a), 0, 1);
		node1.addChild(node2);
		return node1;
	}

}