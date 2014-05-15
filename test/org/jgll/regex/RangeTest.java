package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class RangeTest {
	
	@Test
	public void overlappingTest1() {
		Range r1 = Range.in('a', 'f');
		Range r2 = Range.in('d', 'k');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}
	
	@Test
	public void overlappingTest2() {
		Range r1 = Range.in('a', 'z');
		Range r2 = Range.in('s', 'u');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}
	
	
	@Test
	public void test() {
		RegularExpression regexp = Range.in('0', '9');
		Automaton nfa = regexp.getAutomaton();
		assertEquals(2, nfa.getCountStates());
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("0")));
		assertEquals(1, dfa.match(Input.fromString("0"), 0));
	}
	
	@Test
	public void testWithPreConditions() {
		RegularExpression regexp = Range.in('0', '9').withCondition(RegularExpressionCondition.notFollow(Character.from(':')));
		Automaton nfa = regexp.getAutomaton();
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();

		assertEquals(-1, dfa.match(Input.fromString("0:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("9:"), 0));
	}
	

}
