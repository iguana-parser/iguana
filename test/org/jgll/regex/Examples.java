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
		Range az = new Range('a', 'z');
		Range AZ = new Range('A', 'Z');
		Range zero_9 = new Range('0', '9');
		CharacterClass c1 = new CharacterClass(az, AZ);
		CharacterClass c2 = new CharacterClass(az, AZ, zero_9);
		
		RegularExpression regexp = new Sequence(c1, new RegexStar(c2));
		NFA nfa = regexp.toNFA();
		
		DFA dfa = nfa.toDFA();

		assertTrue(dfa.match(Input.fromString("a")));
		
	}

}
