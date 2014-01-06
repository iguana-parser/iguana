package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexOpt(new Character('a'));
		Automaton nfa = regexp.toNFA();
		assertEquals(4, nfa.getCountStates());

		DFA dfa = nfa.toDFA();
		assertTrue(dfa.match(Input.fromString("a")));
		assertTrue(dfa.match(Input.fromString("")));
	}

}
