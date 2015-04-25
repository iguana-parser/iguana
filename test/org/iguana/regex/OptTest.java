package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.regex.Opt;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
import org.junit.Test;

public class OptTest {
	
	@Test
	public void test1() {
		RegularExpression regex = Opt.from(Character.from('a'));
		Automaton automaton = regex.getAutomaton();
		assertEquals(4, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(2, automaton.getCountStates());

		Matcher matcher = new DFAMatcher(automaton);
		assertTrue(matcher.match(Input.fromString("a")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}
	
	@Test
	public void test2() {
		RegularExpression regex = Opt.from(Sequence.from("integer"));
		Automaton automaton = regex.getAutomaton();
		assertEquals(10, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);		
		assertEquals(8, automaton.getCountStates());

		Matcher matcher = MatcherFactory.getMatcher(regex);
		assertTrue(matcher.match(Input.fromString("integer")));
		assertFalse(matcher.match(Input.fromString("int")));
		assertTrue(matcher.matchPrefix(Input.fromString("int")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}

}
