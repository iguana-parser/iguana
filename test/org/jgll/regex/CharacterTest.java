package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new Character('a');
		Automaton nfa = regexp.toAutomaton();
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertEquals(2, nfa.getCountStates());
		assertTrue(dfa.match(Input.fromString("a")));
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
	}

}
