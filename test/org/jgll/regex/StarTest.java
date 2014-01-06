package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new RegexStar(new Character('a'));
		Automaton nfa = regexp.toNFA();
		
		Matcher dfa = nfa.getMatcher();
		
		assertEquals(4, nfa.getCountStates());
		
		assertEquals(0, dfa.match(Input.fromString(""), 0));
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(2, dfa.match(Input.fromString("aa"), 0));
		assertEquals(3, dfa.match(Input.fromString("aaa"), 0));
		assertEquals(6, dfa.match(Input.fromString("aaaaaa"), 0));
		assertEquals(17, dfa.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, dfa.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));
		
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
	}

}
