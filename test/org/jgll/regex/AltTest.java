package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class AltTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexAlt(new Character('a'), new Character('b'));
		Automaton nfa = regexp.toNFA();
		assertEquals(6, nfa.getCountStates());
		
		DFA dfa = nfa.toDFA();
		assertTrue(dfa.match(Input.fromString("a")));
		assertTrue(dfa.match(Input.fromString("b")));
	}
}