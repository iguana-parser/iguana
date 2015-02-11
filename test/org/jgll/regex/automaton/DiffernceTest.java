package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Alt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.regex.Sequence;
import org.jgll.regex.matcher.DFAMatcher;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;



public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Sequence<Character> k1 = Sequence.from("aa");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), k1.getAutomaton());
		Visualization.generateAutomatonGraph("/Users/aliafroozeh/output", a);
		
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("i")));
		assertTrue(matcher.match(Input.fromString("iif")));
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("if:")));
		assertFalse(matcher.match(Input.fromString("first:")));
	}
	
	@Test
	public void test2() {
		Automaton union = AutomatonOperations.union(k1.getAutomaton(), k2.getAutomaton());
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), union);

		DFAMatcher matcher = new DFAMatcher(a);
		System.out.println(matcher.match(Input.fromString("f"), 0));
		
		assertEquals(5, matcher.match(Input.fromString("first"), 0));
		assertEquals(-1, matcher.match(Input.fromString("if"), 0));
		assertEquals(-1, matcher.match(Input.fromString("when"), 0));
	}
	
	@Test
	public void test3() {
		Alt<Sequence<Character>> alt = Alt.from(k1, k2, k3);
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), alt.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertEquals(5, matcher.match(Input.fromString("first"), 0));
		assertEquals(-1, matcher.match(Input.fromString("if"), 0));
		assertEquals(-1, matcher.match(Input.fromString("when"), 0));
		assertEquals(-1, matcher.match(Input.fromString("new"), 0));
	}

}
