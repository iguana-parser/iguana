package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonBuilder.*;
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
		Automaton a = union(k1.getAutomaton(), k2.getAutomaton());
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("if")));
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("when")));
		assertFalse(a.getRunnableAutomaton().match(Input.fromString("else")));
	}
	
	@Test
	public void test3() {
		Automaton a = union(k1.getAutomaton(), union(k2.getAutomaton(), k3.getAutomaton()));
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("if")));
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("when")));
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("new")));
		assertFalse(a.getRunnableAutomaton().match(Input.fromString("else")));
	}

}
