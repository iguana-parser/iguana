package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Sequence;
import org.jgll.util.Input;
import org.junit.Test;

public class UnionTest {
	
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = k1.getAutomaton().builder().union(k2.getAutomaton()).build();
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertFalse(matcher.match(Input.fromString("else")));
	}
	
	@Test
	public void test3() {
		Automaton a = k1.getAutomaton().builder().union(k2.getAutomaton(), k3.getAutomaton()).build();
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertTrue(matcher.match(Input.fromString("new")));
		assertFalse(matcher.match(Input.fromString("else")));
	}

}
