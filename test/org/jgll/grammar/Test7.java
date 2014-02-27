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
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
public class Test7 {

	private Grammar grammar;
	
	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		
		Character a = new Character('a');
		Character b = new Character('b');
		
		Rule r1 = new Rule(S, list(A, A, b));
		Rule r3 = new Rule(A, list(a));
		Rule r4 = new Rule(A);
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		grammar = new GrammarBuilder("test5", factory).addRule(r1).addRule(r3).addRule(r4).build();
	}
	
	@Test
	public void test1() throws ParseError {
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf1 = parser.parse(input, grammar, "S");
	}	
}
	