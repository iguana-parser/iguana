package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class NFATestRange {
	
	@Test
	public void testCountStates() {
		RegularExpression regexp = new Range('0', '9');
		NFA nfa = regexp.toNFA();
		assertEquals(2, nfa.getCountStates());
		DFA dfa = nfa.toDFA();
		System.out.println(dfa.match(Input.fromString("0")));
	}

}
