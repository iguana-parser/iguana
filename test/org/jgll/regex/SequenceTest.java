package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

public class SequenceTest {
	
	private RegularExpression seq1;
	private RegularExpression seq2;
	private RegularExpression seq3;

	@Before
	public void init() {
		seq1 = Sequence.from(Character.from('a'), Character.from('b'));		
		seq2 = Sequence.from(Range.in('a', 'z'), Range.in('0', '9'));
		seq3 = Sequence.from(Range.in('a', 'z'), Range.in('b', 'm'));
	}
	
	@Test
	public void test1() {
		Automaton nfa = seq1.getAutomaton();
		
		assertEquals(4, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromString("ab")));
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	
	@Test
	public void test2() {
		Automaton nfa = seq2.getAutomaton();

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
		Automaton nfa = seq3.getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertTrue(matcher.match(Input.fromString("dm")));
	}
	
	@Test
	public void test1WithPostCondition() {
		// [a][b] !>> [c]
		RegularExpression r = seq1.withCondition(RegularExpressionCondition.notFollow(Character.from('c')));
		RunnableAutomaton dfa = r.getAutomaton().getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("abc"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		RegularExpression r = seq2.withCondition(RegularExpressionCondition.notFollow(Character.from(':')));

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
		RegularExpression r = seq3.withCondition(RegularExpressionCondition.notFollow(Character.from(':')));
		Automaton nfa = r.getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertEquals(-1, matcher.match(Input.fromString("dm:"), 0));
	}

	
}
