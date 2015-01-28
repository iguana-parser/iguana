package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Sequence;
import org.jgll.util.Input;
import org.junit.Test;

public class ReverseAutomatonTest {
	
	@Test
	public void test() {
		Sequence<Character> r = Sequence.from("test");
		Automaton a = r.getAutomaton().reverse();
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("tset")));
	}

}
