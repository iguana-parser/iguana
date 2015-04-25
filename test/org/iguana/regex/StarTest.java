package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.regex.Alt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.regex.matcher.Matcher;
import org.iguana.util.Input;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test1() {
		RegularExpression regex = Star.from(Character.from('a'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(4, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(2, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(2, matcher.match(Input.fromString("aa"), 0));
		assertEquals(3, matcher.match(Input.fromString("aaa"), 0));
		assertEquals(6, matcher.match(Input.fromString("aaaaaa"), 0));
		assertEquals(17, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));
	}
		
	@Test
	public void test2() {
		// ([a-a]+)*
		RegularExpression regex = Star.from(Sequence.from(Plus.from(Alt.from(CharacterRange.in('a', 'a')))));
		Automaton automaton = regex.getAutomaton();
		assertEquals(7, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());

		Matcher matcher = new DFAMatcher(automaton);
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(5, matcher.match(Input.fromString("aaaaa"), 0));
	}
	
	
	@Test
	public void test3() {
		// ([a-z]+ | [(-)] | "*")*
		RegularExpression r1 = Plus.from(Alt.from(CharacterRange.in('a', 'z')));
		RegularExpression r2 = Plus.from(Alt.from(CharacterRange.in('(', ')')));
		RegularExpression r3 = Character.from('*');
		
		Automaton automaton = Star.from(Alt.from(r1, r2, r3)).getAutomaton();
		assertEquals(16, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(6, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(1, matcher.match(Input.fromString("m"), 0));
		assertEquals(1, matcher.match(Input.fromString("z"), 0));
		assertEquals(1, matcher.match(Input.fromString("*"), 0));
		assertEquals(1, matcher.match(Input.fromString("("), 0));
		assertEquals(1, matcher.match(Input.fromString(")"), 0));
		assertEquals(3, matcher.match(Input.fromString("a)*"), 0));
		assertEquals(3, matcher.match(Input.fromString("*(a"), 0));
		assertEquals(3, matcher.match(Input.fromString(")*a"), 0));
		assertEquals(11, matcher.match(Input.fromString("ab(*za()((a"), 0));
	}
	

}