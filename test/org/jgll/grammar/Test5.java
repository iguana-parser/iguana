package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= 'a' A 'c'
 *     | 'a' A 'b'
 *     
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 *
 */
public class Test5 {

	private Grammar grammar;
	
	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		Rule r1 = new Rule(S, list(a, A, c));
		Rule r2 = new Rule(S, list(a, A, b));
		Rule r3 = new Rule(A, list(a));
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		grammar = new GrammarBuilder("test5", factory).addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("aab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf1 = parser.parse(input, grammar, "S");
	}	
}
	