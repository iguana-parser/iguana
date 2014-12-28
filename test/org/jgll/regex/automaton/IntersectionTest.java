package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
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
		Automaton a1 = new Automaton(x0, "a1");
		
		State y0 = new State(StateType.FINAL);
		State y1 = new State();
		
		y0.addTransition(new Transition('0', y1));
		y0.addTransition(new Transition('1', y1));
		y1.addTransition(new Transition('0', y0));
		y1.addTransition(new Transition('1', y0));
		
		// Matches an string of even length.
		Automaton a2 = new Automaton(y0, "a2");
		
		a1.intersection(a2);

		assertFalse(a1.isLanguageEmpty());
		assertTrue(a1.getRunnableAutomaton().match(Input.fromString("111001110001")));
	}
	
	@Test
	public void test2() {
		RegularExpression f = RegularExpressionExamples.getFloat().build();
		RegularExpression id = RegularExpressionExamples.getId().build();
		
		// Should not overlap, therefore the intersection should be empty.
		assertTrue(f.getAutomaton().intersection(id.getAutomaton()).isLanguageEmpty());
	}
	
	@Test
	public void test3() {
		Keyword k1 = Keyword.from("for");
		Keyword k2 = Keyword.from("forall");

		assertTrue(AutomatonOperations.prefix(k1.getAutomaton(), k2.getAutomaton()));
		assertFalse(AutomatonOperations.prefix(k2.getAutomaton(), k1.getAutomaton()));
	}
	
}
