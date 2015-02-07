package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Constants;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	public void test1() {
		RegularExpression regexp = Alt.from(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'));
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());

		Matcher matcher = regexp.getMatcher();
		
		assertTrue(matcher.match(Input.fromChar('a')));
		assertTrue(matcher.match(Input.fromChar('f')));
		assertTrue(matcher.match(Input.fromChar('z')));
		assertTrue(matcher.match(Input.fromChar('1')));
		assertTrue(matcher.match(Input.fromChar('5')));
		assertTrue(matcher.match(Input.fromChar('8')));
		
		assertFalse(matcher.match(Input.fromChar('0')));
		assertFalse(matcher.match(Input.fromChar('9')));
		assertFalse(matcher.match(Input.fromChar('*')));
	}
	
	public void test2() {
		RegularExpression regexp = Alt.from(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'));
		Automaton nfa = regexp.getAutomaton();

		assertEquals(8, nfa.getCountStates());

		Matcher matcher = regexp.getMatcher();
		
		assertTrue(matcher.match(Input.fromChar('1')));
		assertTrue(matcher.match(Input.fromChar('2')));
		assertTrue(matcher.match(Input.fromChar('3')));
		assertTrue(matcher.match(Input.fromChar('4')));
		assertTrue(matcher.match(Input.fromChar('5')));
		assertTrue(matcher.match(Input.fromChar('6')));
		assertTrue(matcher.match(Input.fromChar('7')));
		assertTrue(matcher.match(Input.fromChar('8')));
		
		assertFalse(matcher.match(Input.fromChar('0')));
		assertFalse(matcher.match(Input.fromChar('9')));
	}
	
	public void notTest() {
		Alt<CharacterRange> c = Alt.from(CharacterRange.in('0', '9'), CharacterRange.in('a', 'z'));
		Alt<CharacterRange> expected = Alt.from(CharacterRange.in(1, '0' - 1), 
													  CharacterRange.in('9' + 1, 'a' - 1), 
													  CharacterRange.in('z' + 1, Constants.MAX_UTF32_VAL));
		
		assertEquals(expected, Alt.not(c));
	}
	
	public void test1WithPostConditions() {
		RegularExpression regexp = Alt.builder(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'))
								       .addPreCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());

		Matcher matcher = regexp.getMatcher();
		
		assertEquals(-1, matcher.match(Input.fromString("a:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("f:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("z:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("1:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("5:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("8:"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		RegularExpression regexp = Alt.builder(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'))
								   .addPreCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		
		Automaton nfa = regexp.getAutomaton();
		
		Matcher matcher = regexp.getMatcher();
		
		assertEquals(-1, matcher.match(Input.fromString("1:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("2:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("3:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("4:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("5:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("6:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("7:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("8:"), 0));
	}

	
}
