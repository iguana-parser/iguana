package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.regex.matcher.DFAMatcher;
import org.jgll.util.Input;
import org.junit.Test;

public class IntersectionTest {

	@Test
	public void test1() {
		
		State x0 = new State();
		State x1 = new State(StateType.FINAL);
		
		x0.addTransition(new Transition('0', x0));
		x0.addTransition(new Transition('1', x1));
		x1.addTransition(new Transition('0', x1));
		x1.addTransition(new Transition('1', x0));
		
		// Matches an odd number of 1's.
		Automaton a1 = Automaton.builder(x0).build();
		
		State y0 = new State(StateType.FINAL);
		State y1 = new State();
		
		y0.addTransition(new Transition('0', y1));
		y0.addTransition(new Transition('1', y1));
		y1.addTransition(new Transition('0', y0));
		y1.addTransition(new Transition('1', y0));
		
		// Matches an string of even length.
		Automaton a2 = Automaton.builder(y0).build();
		
		Automaton intersect = AutomatonOperations.intersect(a1, a2);
		DFAMatcher matcher = new DFAMatcher(intersect);
		
		assertFalse(intersect.isLanguageEmpty());
		assertTrue(matcher.match(Input.fromString("111001110001")));
		assertFalse(matcher.match(Input.fromString("111001010001")));
		assertFalse(matcher.match(Input.fromString("1110011100010")));
	}
	
	@Test
	public void test2() {
		RegularExpression f = RegularExpressionExamples.getFloat();
		RegularExpression id = RegularExpressionExamples.getId();
		
		Automaton intersect = AutomatonOperations.intersect(f.getAutomaton(), id.getAutomaton());
		
		// Should not overlap, therefore the intersection should be empty.
		assertTrue(intersect.isLanguageEmpty());
	}
		
}
