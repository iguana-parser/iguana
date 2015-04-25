package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Constants;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	@Test
	public void test1() {
		RegularExpression regex = Alt.from(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(6, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		
		Matcher matcher = MatcherFactory.getMatcher(regex);
		
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
	
	@Test
	public void test2() {
		RegularExpression regex = Alt.from(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(8, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(5, automaton.getCountStates());

		Matcher matcher = MatcherFactory.getMatcher(regex);
		
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
	
}
