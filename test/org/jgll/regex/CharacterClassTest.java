package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Constants;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class CharacterClassTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('1', '8'));
		Automaton nfa = regexp.toAutomaton();
		
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
	
	@Test
	public void test2() {
		RegularExpression regexp = new CharacterClass(new Range('1', '5'), new Range('1', '7'), new Range('3', '8'));
		Automaton nfa = regexp.toAutomaton();

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
	
	@Test
	public void notTest() {
		CharacterClass c = new CharacterClass(Range.in('0', '9'), Range.in('a', 'z'));
		CharacterClass expected = new CharacterClass(Range.in(1, '0' - 1), 
													 Range.in('9' + 1, 'a' - 1), 
													 Range.in('z' + 1, Constants.MAX_UTF32_VAL));
		
		assertEquals(expected, c.not());
	}
	
	@Test
	public void test1WithPostConditions() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('1', '8'))
								       .addCondition(RegularExpressionCondition.notFollow(new Character(':')));
		Automaton nfa = regexp.toAutomaton();
		
		assertEquals(6, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertEquals(-1, dfa.match(Input.fromString("a:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("f:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("z:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("1:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("8:"), 0));
	}

	
}
