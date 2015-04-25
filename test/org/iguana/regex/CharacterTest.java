package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
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
		Matcher matcher = MatcherFactory.getMatcher(c);
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
