package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.DFAMatcher;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterTest {

	Character c = Character.from('a');
	
	@Test
	public void testAutomaton() {
		Automaton a = c.getAutomaton();
		assertArrayEquals(new CharacterRange[] {CharacterRange.in('a', 'a')}, a.getAlphabet());
		assertEquals(2, a.getCountStates());
	}
	
	@Test
	public void testMatcher() {
		Matcher matcher = c.getMatcher();
		Input input = Input.fromString("a");
		assertTrue(matcher.match(input));
		assertEquals(1, matcher.match(input, 0));
	}
	
	@Test
	public void testDFAMatcher() {
		Automaton automaton = c.getAutomaton();
		DFAMatcher matcher = new DFAMatcher(automaton);
		Input input = Input.fromString("a");
		assertEquals(1, matcher.match(input, 0));
	}

}
