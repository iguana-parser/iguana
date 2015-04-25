package org.iguana.regex.automaton;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.RegularExpressionExamples;
import org.iguana.regex.Sequence;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.util.Input;
import org.junit.Test;



public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), k1.getAutomaton());
		
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
		
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("when")));
	}
	
	@Test
	public void test3() {
		Alt<Sequence<Character>> alt = Alt.from(k1, k2, k3);
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), alt.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("when")));
		assertFalse(matcher.match(Input.fromString("new")));
	}

}
