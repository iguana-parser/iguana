package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

public class SequenceTest {
	
	private Sequence.Builder<Character> seq1;
	private Sequence.Builder<CharacterRange> seq2;
	private Sequence.Builder<CharacterRange> seq3;

	@Before
	public void init() {
		seq1 = Sequence.builder(Character.from('a'), Character.from('b'));		
		seq2 = Sequence.builder(CharacterRange.in('a', 'z'), CharacterRange.in('0', '9'));
		seq3 = Sequence.builder(CharacterRange.in('a', 'z'), CharacterRange.in('b', 'm'));
	}
	
	@Test
	public void test1() {
		Automaton nfa = seq1.build().getAutomaton();
		
		assertEquals(4, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromString("ab")));
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	
	@Test
	public void test2() {
		Automaton nfa = seq2.build().getAutomaton();

		assertEquals(4, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromString("a0")));
		assertTrue(dfa.match(Input.fromString("a5")));
		assertTrue(dfa.match(Input.fromString("a9")));
		assertTrue(dfa.match(Input.fromString("c7")));
		assertTrue(dfa.match(Input.fromString("z0")));
		assertTrue(dfa.match(Input.fromString("z9")));
		
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	/**
	 * Two character classes with overlapping ranges
	 */
	@Test
	public void test3() {
		Automaton nfa = seq3.build().getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertTrue(matcher.match(Input.fromString("dm")));
	}
	
	@Test
	public void test1WithPostCondition() {
		// [a][b] !>> [c]
		Sequence<Character> r = seq1.addPostCondition(RegularExpressionCondition.notFollow(Character.from('c'))).build();
		RunnableAutomaton dfa = r.getAutomaton().getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("abc"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		Sequence<CharacterRange> r = seq2.addPostCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();

		RunnableAutomaton dfa = r.getAutomaton().getRunnableAutomaton();
		
		assertEquals(-1, dfa.match(Input.fromString("a0:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("a5:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("a9:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("c7:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("z0:"), 0));
		assertEquals(-1, dfa.match(Input.fromString("a9:"), 0));
	}

	@Test
	public void test3WithPostConditions() {
		RegularExpression r = seq3.addPostCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = r.getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertEquals(-1, matcher.match(Input.fromString("dm:"), 0));
	}

	
}
