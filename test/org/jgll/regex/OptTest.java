package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.matcher.Matcher;
import org.jgll.regex.matcher.MatcherFactory;
import org.jgll.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test1() {
		RegularExpression regex = Opt.from(Character.from('a'));
		Automaton automaton = regex.getAutomaton();
		assertEquals(2, automaton.getCountStates());

		Matcher matcher = MatcherFactory.getMatcher(regex);
		assertTrue(matcher.match(Input.fromString("a")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}
	
	@Test
	public void test2() {
		RegularExpression regex = Opt.from(Sequence.from("integer"));
		Automaton automaton = regex.getAutomaton();
		assertEquals(8, automaton.getCountStates());

		Matcher matcher = MatcherFactory.getMatcher(regex);
		assertTrue(matcher.match(Input.fromString("integer")));
		assertFalse(matcher.match(Input.fromString("int")));
		assertTrue(matcher.matchPrefix(Input.fromString("int")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}

}
