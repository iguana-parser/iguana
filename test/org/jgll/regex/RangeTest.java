package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class RangeTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new Range('0', '9');
		Automaton nfa = regexp.toAutomaton();
		assertEquals(2, nfa.getCountStates());
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("0")));
		assertEquals(1, dfa.match(Input.fromString("0"), 0));
	}

}
