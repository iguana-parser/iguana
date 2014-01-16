package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new Character('a');
		Automaton nfa = regexp.toAutomaton();
		Matcher dfa = nfa.getMatcher();
		assertEquals(2, nfa.getCountStates());
		assertTrue(dfa.match(Input.fromString("a")));
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
	}
	
	@Test
	public void testMatchAction() {
		RegularExpression regexp = new Character('a');
		Automaton nfa = regexp.toAutomaton();
		
		final int[] l = new int[1];
		
		nfa.addMatchAction(new MatchAction() {
			
			@Override
			public void execute(int length, int state) {
				l[0] = length;
			}
		});

		Matcher dfa = nfa.getMatcher();
		assertTrue(dfa.match(Input.fromString("a")));
		
		assertEquals(1, l[0]);
	}

}
