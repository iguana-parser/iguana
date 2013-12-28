package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class RangeTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new Range('0', '9');
		NFA nfa = regexp.toNFA();
		assertEquals(2, nfa.getCountStates());
		DFA dfa = nfa.toDFA();
		assertTrue(dfa.match(Input.fromString("0")));
		assertEquals(1, dfa.run(Input.fromString("0"), 0));
	}

}
