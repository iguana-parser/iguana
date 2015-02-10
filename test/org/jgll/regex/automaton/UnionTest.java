package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonOperations.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Sequence;
import org.jgll.regex.matcher.DFAMatcher;
import org.jgll.util.Input;
import org.junit.Test;

public class UnionTest {
	
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = union(k1.getAutomaton(), k2.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertFalse(matcher.match(Input.fromString("i")));
		assertFalse(matcher.match(Input.fromString("w")));
		assertFalse(matcher.match(Input.fromString("wh")));
		assertFalse(matcher.match(Input.fromString("whe")));
		assertFalse(matcher.match(Input.fromString("whenever")));
		assertFalse(matcher.match(Input.fromString("else")));
	}
	
	@Test
	public void test3() {
		Automaton a = union(k1.getAutomaton(), union(k2.getAutomaton(), k3.getAutomaton()));
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertTrue(matcher.match(Input.fromString("new")));
		assertFalse(matcher.match(Input.fromString("i")));
		assertFalse(matcher.match(Input.fromString("w")));
		assertFalse(matcher.match(Input.fromString("n")));
		assertFalse(matcher.match(Input.fromString("ne")));
		assertFalse(matcher.match(Input.fromString("news")));
		assertFalse(matcher.match(Input.fromString("else")));
	}

}
