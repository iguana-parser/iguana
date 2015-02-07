package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterTest {

	Character c = Character.from('a');
	
	@Test
	public void testAutomaton() {
		Automaton a = c.getAutomaton();
		assertEquals(2, a.getCountStates());
	}
	
	@Test
	public void testMatcher() {
		Matcher matcher = c.getMatcher();
		Input input = Input.fromString("a");
		assertTrue(matcher.match(input));
		assertEquals(1, matcher.match(input, 0));
	}

}
