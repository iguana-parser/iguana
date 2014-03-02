package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * A ::= "if" B
 * 
 * B ::= [b]
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordTest2 {
	
	private Grammar grammar;
	
	Nonterminal A = new Nonterminal("A");
	Nonterminal B = new Nonterminal("B");
	Keyword iff = new Keyword("if", new int[] {'i', 'f'});

	@Before
	public void init() {
		
		Rule r1 = new Rule(A, list(iff, B));
		Rule r2 = new Rule(B, new Character('b'));
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder(factory);
		
		builder.addRule(r1);
		builder.addRule(r2);
		builder.addRule(GrammarBuilder.fromKeyword(iff));
		
		grammar = builder.build();
	}
	
	@Test
	public void testFirstSet() {
		assertEquals(set(iff), grammar.getFirstSet(grammar.getNonterminalByName("A")));
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("ifb");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		parser.parse(input, grammar, "A");
	}
	
}
