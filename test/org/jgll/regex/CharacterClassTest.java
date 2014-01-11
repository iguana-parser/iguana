package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('1', '8'));
		Automaton nfa = regexp.toNFA();
		
		assertEquals(6, nfa.getCountStates());

		Matcher dfa = nfa.getMatcher();
		
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
	
	@Test
	public void test2() {
		RegularExpression regexp = new CharacterClass(new Range('1', '5'), new Range('1', '7'), new Range('3', '8'));
		Automaton nfa = regexp.toNFA();

		assertEquals(8, nfa.getCountStates());

		Matcher dfa = nfa.getMatcher();
		
		assertTrue(dfa.match(Input.fromChar('1')));
		assertTrue(dfa.match(Input.fromChar('2')));
		assertTrue(dfa.match(Input.fromChar('3')));
		assertTrue(dfa.match(Input.fromChar('4')));
		assertTrue(dfa.match(Input.fromChar('5')));
		assertTrue(dfa.match(Input.fromChar('6')));
		assertTrue(dfa.match(Input.fromChar('7')));
		assertTrue(dfa.match(Input.fromChar('8')));
		
		assertFalse(dfa.match(Input.fromChar('0')));
		assertFalse(dfa.match(Input.fromChar('9')));
	}
	
	@Test
	public void notTest() {
		CharacterClass c = new CharacterClass(Range.in('0', '9'), Range.in('a', 'z'));
		System.out.println(c.not());
	}
	
}
