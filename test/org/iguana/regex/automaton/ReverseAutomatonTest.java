package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.Alt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.regex.matcher.DFAMatcher;
import org.jgll.util.Input;
import org.junit.Test;

public class ReverseAutomatonTest {
	
	@Test
	public void test1() {
		Sequence<Character> r = Sequence.from("test");
		Automaton a = AutomatonOperations.reverse(r.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("tset")));
	}
	
	@Test
	public void test2() {
		RegularExpression r = Alt.from(CharacterRange.in('a', 'z'), CharacterRange.in('A', 'Z'), CharacterRange.in('0', '9'), Character.from('_'));
		Automaton a = AutomatonOperations.reverse(r.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromChar('a')));
		assertTrue(matcher.match(Input.fromChar('m')));
		assertTrue(matcher.match(Input.fromChar('z')));
		assertTrue(matcher.match(Input.fromChar('A')));
		assertTrue(matcher.match(Input.fromChar('M')));
		assertTrue(matcher.match(Input.fromChar('Z')));
		assertTrue(matcher.match(Input.fromChar('0')));
		assertTrue(matcher.match(Input.fromChar('9')));
		assertTrue(matcher.match(Input.fromChar('3')));
		assertTrue(matcher.match(Input.fromChar('_')));
	}

}
