package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.junit.Test;

public class NFATestCharacter {
	
	@Test
	public void testCountStates() {
		RegularExpression regexp = new Character('a');
		NFA nfa = regexp.toNFA();
		assertEquals(2, nfa.getCountStates());
	}

}
