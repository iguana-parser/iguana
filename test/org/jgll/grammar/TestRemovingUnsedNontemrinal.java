package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.junit.Before;


/**
 * 
 * 
 * S ::= B C
 *     | D
 * 
 * E ::= 'e'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'd'
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TestRemovingUnsedNontemrinal {

	private Grammar grammar;

	@Before
	public void init() {
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("TwoLevelFiltering", factory);
		
		Character b = new Character('b');
		Character c = new Character('c');
		Character d = new Character('d');
		Character e = new Character('e');
		Nonterminal S = new Nonterminal("S");
		Nonterminal B = new Nonterminal("B");
		Nonterminal C = new Nonterminal("C");
		Nonterminal D = new Nonterminal("D");
		Nonterminal E = new Nonterminal("E");
		
		builder.addRule(new Rule(S, list(B, C)));
		builder.addRule(new Rule(S, list(D)));
		builder.addRule(new Rule(B, list(b)));
		builder.addRule(new Rule(C, list(c)));
		builder.addRule(new Rule(D, list(d)));
		builder.addRule(new Rule(E, list(e)));

		grammar = builder.removeUnusedNonterminals(S).build();
	}	
	
}
