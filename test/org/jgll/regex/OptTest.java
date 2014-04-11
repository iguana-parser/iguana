package org.jgll.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexOpt(new Character('a'));
		Automaton nfa = regexp.toAutomaton();
		assertEquals(4, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("a")));
		assertEquals(0, dfa.match(Input.fromString(""), 0));
	}

}
