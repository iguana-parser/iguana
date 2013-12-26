package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.junit.Test;

public class NFATestRange {
	
	@Test
	public void testCountStates() {
		RegularExpression regexp = new Range('0', '9');
		NFA nfa = regexp.toNFA();
		DFA dfa = nfa.toDFA();
		assertEquals(2, nfa.getCountStates());
		System.out.println(nfa.toJavaCode());
	}

}
