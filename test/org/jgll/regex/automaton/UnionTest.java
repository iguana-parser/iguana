package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonOperations.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.util.Input;
import org.junit.Test;

public class UnionTest {
	
	private Keyword k1 = Keyword.from("if");
	private Keyword k2 = Keyword.from("when");
	private Keyword k3 = Keyword.from("new");

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
