package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class PlusTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexPlus(new Character('a'));
		Automaton nfa = regexp.toNFA();
		DFA dfa = nfa.toDFA();

		assertEquals(8, nfa.getCountStates());
		
		
		assertEquals(1, dfa.run(Input.fromString("a"), 0));
		assertEquals(2, dfa.run(Input.fromString("aa"), 0));
		assertEquals(3, dfa.run(Input.fromString("aaa"), 0));
		assertEquals(6, dfa.run(Input.fromString("aaaaaa"), 0));
		assertEquals(17, dfa.run(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, dfa.run(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));

		
		assertFalse(dfa.match(Input.fromString("")));
	}

}
