package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Constants;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	public void test1() {
		RegularExpression regexp = CharacterClass.from(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'));
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromChar('a')));
		assertTrue(dfa.match(Input.fromChar('f')));
		assertTrue(dfa.match(Input.fromChar('z')));
		assertTrue(dfa.match(Input.fromChar('1')));
		assertTrue(dfa.match(Input.fromChar('5')));
		assertTrue(dfa.match(Input.fromChar('8')));
		
		assertFalse(dfa.match(Input.fromChar('0')));
		assertFalse(dfa.match(Input.fromChar('9')));
		assertFalse(dfa.match(Input.fromChar('*')));
	}
	
	public void test2() {
		RegularExpression regexp = CharacterClass.from(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'));
		Automaton nfa = regexp.getAutomaton();

		assertEquals(8, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromChar('1')));
		assertTrue(dfa.match(Input.fromChar('2')));
		assertTrue(dfa.match(Input.fromChar('3')));
		assertTrue(dfa.match(Input.fromChar('4')));
		assertTrue(dfa.match(Input.fromChar('5')));
		assertTrue(dfa.match(Input.fromChar('6')));
		assertTrue(dfa.match(Input.fromChar('7')));
		assertTrue(dfa.match(Input.fromChar('8')));
		
		assertFalse(dfa.match(Input.fromChar('0')));
		assertFalse(dfa.match(Input.fromChar('9')));
	}
	
	public void notTest() {
		CharacterClass c = CharacterClass.from(CharacterRange.in('0', '9'), CharacterRange.in('a', 'z'));
		CharacterClass expected = CharacterClass.from(CharacterRange.in(1, '0' - 1), 
													  CharacterRange.in('9' + 1, 'a' - 1), 
													  CharacterRange.in('z' + 1, Constants.MAX_UTF32_VAL));
		
		assertEquals(expected, c.not());
	}
	
	public void test1WithPostConditions() {
		RegularExpression regexp = new CharacterClass.Builder(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'))
								       .addCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertEquals(-1, dfa.match(Input.fromString("a:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("f:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("z:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("1:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("8:"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		RegularExpression regexp = new CharacterClass.Builder(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'))
								   .addCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		
		Automaton nfa = regexp.getAutomaton();
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertEquals(-1, dfa.match(Input.fromString("1:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("2:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("3:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("4:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("6:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("7:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("8:"), 0));
	}

	
}
