package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new Sequence(new Character('a'), new Character('b'));
		NFA nfa = regexp.toNFA();

		assertEquals(6, nfa.getCountStates());

		DFA dfa = nfa.toDFA();
		
		assertTrue(dfa.match(Input.fromString("ab")));
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	
	@Test
	public void test2() {
		RegularExpression regexp = new Sequence(new Range('a', 'z'), new Range('0', '9'));
		NFA nfa = regexp.toNFA();

		assertEquals(6, nfa.getCountStates());

		DFA dfa = nfa.toDFA();
		
		assertTrue(dfa.match(Input.fromString("a0")));
		assertTrue(dfa.match(Input.fromString("a5")));
		assertTrue(dfa.match(Input.fromString("a9")));
		assertTrue(dfa.match(Input.fromString("c7")));
		assertTrue(dfa.match(Input.fromString("z0")));
		assertTrue(dfa.match(Input.fromString("z9")));
		
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
}
