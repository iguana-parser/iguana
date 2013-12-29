package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('1', '8'));
		NFA nfa = regexp.toNFA();

		assertEquals(6, nfa.getCountStates());

		DFA dfa = nfa.toDFA();
		
		assertTrue(dfa.match(Input.fromChar('a')));
		assertTrue(dfa.match(Input.fromChar('f')));
		assertTrue(dfa.match(Input.fromChar('z')));
		assertTrue(dfa.match(Input.fromChar('1')));
		assertTrue(dfa.match(Input.fromChar('5')));
		assertTrue(dfa.match(Input.fromChar('8')));
		
		assertFalse(dfa.match(Input.fromChar('0')));
		assertFalse(dfa.match(Input.fromChar('9')));
		assertFalse(dfa.match(Input.fromChar('*')));
	}
	
}
