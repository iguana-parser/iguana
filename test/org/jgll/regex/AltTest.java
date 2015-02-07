package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonBuilder;
import org.jgll.util.Input;
import org.junit.Test;


public class AltTest {
	
	@Test
	public void test1() {
		Character a = Character.from('a');
		Character b = Character.from('b');
		
		RegularExpression regex = Alt.from(a, b);
		Automaton nfa = regex.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());
		
		Matcher dfa = regex.getMatcher();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		Sequence<Character> k1 = Sequence.from("for");
		Sequence<Character> k2 = Sequence.from("forall");
		
		Automaton result = AutomatonBuilder.or(k1.getAutomaton(), k2.getAutomaton());

		Matcher dfa = result.getRunnableAutomaton();
		assertEquals(3, dfa.match(Input.fromString("for"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regex = Alt.from(Sequence.from("when"), Sequence.from("if"));

		Matcher dfa = regex.getMatcher();
		
		assertEquals(4, dfa.match(Input.fromString("when"), 0));
		assertEquals(2, dfa.match(Input.fromString("if"), 0));
	}
}
