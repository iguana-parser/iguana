package org.jgll.grammar;

import org.jgll.grammar.slot.IntermediateNodeIds;
import org.jgll.grammar.slot.OriginalIntermediateNodeIds;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.junit.Before;
import org.junit.Test;

import static org.jgll.util.CollectionsUtil.*;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntermediateNodeIdsTest {
	

	private Grammar grammar;

	@Before
	public void init() {
		/**
		 * S ::= A B C
		 *     | A B D
		 *     
		 * A ::= a
		 * B ::= b
		 * C ::= c
		 * D ::= d
		 * 
		 */
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Nonterminal D = Nonterminal.withName("D");
		Character a = Character.from('a');
		Character b = Character.from('b');
		Character c = Character.from('c');
		Character d = Character.from('d');
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Rule rule1 = new Rule(S, list(A, B, C));
		Rule rule2 = new Rule(S, list(A, B, D));
		Rule rule3 = new Rule(A, list(a));
		Rule rule4 = new Rule(B, list(b));
		Rule rule5 = new Rule(C, list(c));
		Rule rule6 = new Rule(D, list(d));
		
		grammar = builder.addRules(rule1, rule2, rule3, rule4, rule5, rule6).build();
	}
	
	@Test
	public void test() {
		IntermediateNodeIds ids = new OriginalIntermediateNodeIds(grammar);
		System.out.println(ids);
	}

}
