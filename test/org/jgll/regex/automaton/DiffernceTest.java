package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonBuilder.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Alt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.regex.Sequence;
import org.jgll.util.Input;
import org.junit.Test;

public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId().build();
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {		
		Automaton a = difference(id.getAutomaton(), k1.getAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if:"), 0));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first:"), 0));
	}
	
	@Test
	public void test2() {
		Automaton a = difference(id.getAutomaton(), union(k1.getAutomaton(), k2.getAutomaton()));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
	}
	
	@Test
	public void test3() {
		Alt alt = Alt.from(k1, k2, k3);
		
		Automaton a = difference(id.getAutomaton(), alt.getAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("new"), 0));
	}

}
