package org.jgll.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class PlusTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexPlus(new Character('a'));
		Automaton nfa = regexp.toAutomaton();

		assertEquals(6, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(2, dfa.match(Input.fromString("aa"), 0));
		assertEquals(3, dfa.match(Input.fromString("aaa"), 0));
		assertEquals(6, dfa.match(Input.fromString("aaaaaa"), 0));
		assertEquals(17, dfa.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, dfa.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));

		
		assertFalse(dfa.match(Input.fromString("")));
	}

}
