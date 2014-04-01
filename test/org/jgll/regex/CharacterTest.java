package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.StateAction;
import org.jgll.regex.matcher.Matcher;
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
		
		final int[] l = new int[1];
		
		regexp.addFinalStateAction(new StateAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(int length, int state) {
				l[0] = length;
			}
		});
		
		Automaton nfa = regexp.toAutomaton();
		
		Matcher dfa = nfa.getMatcher();
		assertTrue(dfa.match(Input.fromString("a")));
		
		assertEquals(1, l[0]);
	}

}
