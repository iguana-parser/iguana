package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class AltTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new RegexAlt<>(new Character('a'), new Character('b'));
		Automaton nfa = regexp.toAutomaton();
		
		assertEquals(6, nfa.getCountStates());
		
		Matcher dfa = nfa.getMatcher();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}	

}