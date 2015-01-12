package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = RegexOpt.from(Character.from('a'));
		Automaton nfa = regexp.getAutomaton();
		assertEquals(4, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("a")));
		assertEquals(0, dfa.match(Input.fromString(""), 0));
	}
	
	@Test
	public void test2() {
		RegularExpression regexp = new RegexOpt.Builder(Character.from('a')).addPostCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = regexp.getAutomaton();
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("a:"), 0));
		assertEquals(-1, dfa.match(Input.fromString(":"), 0));
	}

}
