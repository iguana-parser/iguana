package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.junit.Test;


public class Examples {
	
	/**
	 * Id ::= [a-zA-Z][a-zA-Z0-9]*
	 */
	@Test
	public void test() {
		CharacterClass c1 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'));
		CharacterClass c2 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'), new Range('0', '9'));
		
		RegularExpression regexp = new Sequence(c1, new RegexStar(c2));
		NFA nfa = regexp.toNFA();
		
		DFA dfa = nfa.toDFA();

		assertTrue(dfa.match(Input.fromString("a")));
		assertFalse(dfa.match(Input.fromString("9")));
		assertTrue(dfa.match(Input.fromString("abc")));
		assertTrue(dfa.match(Input.fromString("Identifier")));
		assertTrue(dfa.match(Input.fromString("Identifier12")));
		assertTrue(dfa.match(Input.fromString("Identifier12Assdfd")));
	}

}
