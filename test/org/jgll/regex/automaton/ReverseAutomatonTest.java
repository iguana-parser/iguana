package org.jgll.regex.automaton;

import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;
import org.junit.Test;

public class ReverseAutomatonTest {
	
	@Test
	public void test() {
		RegularExpression r = Keyword.from("test");
		Automaton a = r.getAutomaton().reverse();
		assertTrue(a.getRunnableAutomaton().match(Input.fromString("tset")));
	}

}
