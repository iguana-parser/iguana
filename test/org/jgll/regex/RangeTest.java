package org.jgll.regex;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class RangeTest {
	
	@Test
	public void overlappingTest1() {
		CharacterRange r1 = CharacterRange.in('a', 'f');
		CharacterRange r2 = CharacterRange.in('d', 'k');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}
	
	@Test
	public void overlappingTest2() {
		CharacterRange r1 = CharacterRange.in('a', 'z');
		CharacterRange r2 = CharacterRange.in('s', 'u');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}

	@Test
	public void overlappingToNonOverlapping1() {
		// 1-5 3-7
		CharacterRange r1 = CharacterRange.in(1, 5);
		CharacterRange r2 = CharacterRange.in(3, 7);
		List<CharacterRange> nonOverlapping = CharacterRange.toNonOverlapping(r1, r2);
		System.out.println(nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping2() {
		// 1-7 5-13 6-12 17-21
		CharacterRange r2 = CharacterRange.in(5, 13);
		CharacterRange r4 = CharacterRange.in(17, 21);
		CharacterRange r1 = CharacterRange.in(1, 7);
		CharacterRange r3 = CharacterRange.in(6, 12);
		List<CharacterRange> nonOverlapping = CharacterRange.toNonOverlapping(r1, r2, r3, r4);
		
		// 1-5 6-6 7-7 8-12 13-13 17-21
		assertEquals(Arrays.asList(CharacterRange.in(1, 5), CharacterRange.in(6, 6), CharacterRange.in(7, 7),
					  CharacterRange.in(8, 12), CharacterRange.in(13, 13), CharacterRange.in(17, 21)), nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping3() {
		// 1-7 3-5 4-4
		CharacterRange r1 = CharacterRange.in(1, 7);
		CharacterRange r2 = CharacterRange.in(3, 5);
		CharacterRange r3 = CharacterRange.in(4, 4);
		List<CharacterRange> nonOverlapping = CharacterRange.toNonOverlapping(r1, r2, r3);
		
		// 1-3 4-4 5-5 6-7
		assertEquals(Arrays.asList(CharacterRange.in(1, 3), CharacterRange.in(4, 4), 
								   CharacterRange.in(5, 5), CharacterRange.in(6, 7)), nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping4() {
		// 11-12 1-3 5-7
		CharacterRange r1 = CharacterRange.in(11, 12);
		CharacterRange r2 = CharacterRange.in(1, 3);
		CharacterRange r3 = CharacterRange.in(5, 7);
		List<CharacterRange> nonOverlapping = CharacterRange.toNonOverlapping(r1, r2, r3);
		
		// 1-3 5-7 11-12
		assertEquals(Arrays.asList(CharacterRange.in(1, 3), CharacterRange.in(5, 7), 
								   CharacterRange.in(11, 12)), nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping5() {
		// 7-9 4-11 4-10 1-12 3-6 1-2
		CharacterRange r1 = CharacterRange.in(7, 9);
		CharacterRange r2 = CharacterRange.in(4, 11);
		CharacterRange r3 = CharacterRange.in(4, 10);
		CharacterRange r4 = CharacterRange.in(1, 12);
		CharacterRange r5 = CharacterRange.in(3, 6);
		CharacterRange r6 = CharacterRange.in(1, 2);
		List<CharacterRange> nonOverlapping = CharacterRange.toNonOverlapping(r1, r2, r3, r4, r5, r6);
		
		// 1-3 5-7 11-12
		assertEquals(Arrays.asList(CharacterRange.in(1, 3), 
								   CharacterRange.in(5, 7), 
								   CharacterRange.in(11, 12)), nonOverlapping);
	}

	
	@Test
	public void test() {
		RegularExpression regexp = CharacterRange.in('0', '9');
		Automaton nfa = regexp.getAutomaton();
		assertEquals(2, nfa.getCountStates());
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("0")));
		assertEquals(1, dfa.match(Input.fromString("0"), 0));
	}
	
	@Test
	public void testWithPreConditions() {
		RegularExpression regexp = new CharacterRange.Builder('0', '9').addPreCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = regexp.getAutomaton();
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();

		assertEquals(-1, dfa.match(Input.fromString("0:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("9:"), 0));
	}
	

}
