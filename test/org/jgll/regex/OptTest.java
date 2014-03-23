package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexOpt(new Character('a'));
		Automaton nfa = regexp.toAutomaton();
		assertEquals(4, nfa.getCountStates());

		Matcher dfa = nfa.getMatcher();
		assertTrue(dfa.match(Input.fromString("a")));
		assertTrue(dfa.match(Input.fromString("")));
	}

}
