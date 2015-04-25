package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
import org.junit.Test;


public class AltTest {
	
	@Test
	public void test1() {
		Character a = Character.from('a');
		Character b = Character.from('b');
		
		RegularExpression regex = Alt.from(a, b);
		
		Automaton automaton = regex.getAutomaton();
		assertEquals(6, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		
		Matcher dfa = new DFAMatcher(automaton);
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		Alt<Sequence<Character>> alt = Alt.from(Sequence.from("for"), Sequence.from("forall"));
		Matcher matcher = MatcherFactory.getMatcher(alt);
		assertEquals(3, matcher.match(Input.fromString("for"), 0));
		assertEquals(6, matcher.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regex = Alt.from(Sequence.from("when"), Sequence.from("if"));
		Matcher matcher = MatcherFactory.getMatcher(regex);
		
		assertEquals(4, matcher.match(Input.fromString("when"), 0));
		assertEquals(2, matcher.match(Input.fromString("if"), 0));
	}
	
}
