package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('0', '9'));
		NFA nfa = regexp.toNFA();
		DFA dfa = nfa.toDFA();
//		assertEquals(2, nfa.getCountStates());
//		assertTrue(dfa.match(Input.fromString("a")));
//		assertEquals(1, dfa.run(Input.fromString("a"), 0));
	}

}
