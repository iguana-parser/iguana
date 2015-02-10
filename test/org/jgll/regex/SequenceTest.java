package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.matcher.Matcher;
import org.jgll.regex.matcher.MatcherFactory;
import org.jgll.util.Input;
import org.junit.Test;

public class SequenceTest {
	
	// ab
	private Sequence<Character> seq1 = Sequence.builder(Character.from('a'), Character.from('b')).build();
	
	// [a-z][0-9]
	private Sequence<CharacterRange> seq2 = Sequence.builder(CharacterRange.in('a', 'z'), CharacterRange.in('0', '9')).build();
	
	// [a-z][b-m]
	private Sequence<CharacterRange> seq3 = Sequence.builder(CharacterRange.in('a', 'z'), CharacterRange.in('b', 'm')).build();

	@Test
	public void testAutomaton1() {
		Automaton automaton = seq1.getAutomaton();
		assertEquals(3, automaton.getCountStates());
	}
	
	@Test
	public void testDFAMatcher1() {
		Matcher matcher = MatcherFactory.getMatcher(seq1);
		assertTrue(matcher.match(Input.fromString("ab")));
		assertFalse(matcher.match(Input.fromString("ac")));
		assertFalse(matcher.match(Input.fromString("da")));
	}
	
	@Test
	public void testAutomaton2() {
		Automaton automaton = seq2.getAutomaton();
		assertEquals(3, automaton.getCountStates());
	}
	
	@Test
	public void testDFAMatcher2() {
		Matcher matcher = MatcherFactory.getMatcher(seq2);
		
		assertTrue(matcher.match(Input.fromString("a0")));
		assertTrue(matcher.match(Input.fromString("a5")));
		assertTrue(matcher.match(Input.fromString("a9")));
		assertTrue(matcher.match(Input.fromString("c7")));
		assertTrue(matcher.match(Input.fromString("z0")));
		assertTrue(matcher.match(Input.fromString("z9")));
		
		assertFalse(matcher.match(Input.fromString("ac")));
		assertFalse(matcher.match(Input.fromString("da")));
	}
	
	@Test
	public void testJavaRegexMatcher2() {
		Matcher dfa = MatcherFactory.getMatcher(seq2);
		
		assertTrue(dfa.match(Input.fromString("a0")));
		assertTrue(dfa.match(Input.fromString("a5")));
		assertTrue(dfa.match(Input.fromString("a9")));
		assertTrue(dfa.match(Input.fromString("c7")));
		assertTrue(dfa.match(Input.fromString("z0")));
		assertTrue(dfa.match(Input.fromString("z9")));
		
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}

	
	/**
	 * Two character classes with overlapping ranges
	 */
	@Test
	public void testAutomaon3() {
		Automaton automaton = seq3.getAutomaton();
		assertEquals(3, automaton.getCountStates());		
	}
	
	@Test
	public void testDFAMatcher3() {
		Matcher matcher = MatcherFactory.getMatcher(seq3);
		assertTrue(matcher.match(Input.fromString("dm")));
	}
	
}
