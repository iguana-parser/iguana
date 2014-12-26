package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonOperations.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.RegularExpression;
import org.junit.Test;

public class MergeTransitionsTest {
	
	@Test
	public void test() {
		RegularExpression regexp = CharacterClass.from(CharacterRange.in('0', '4'), CharacterRange.in('5', '7'), CharacterRange.in('8', '9'));
		Automaton a = mergeTransitions(regexp.getAutomaton().minimize());
		assertEquals(a, getAutomaton());
	}
	
	private Automaton getAutomaton() {
		State state1 = new State();
		State state2 = new State(StateType.FINAL);
		state1.addTransition(new Transition(48, 57, state2));
		return new Automaton(state1);
	}

}