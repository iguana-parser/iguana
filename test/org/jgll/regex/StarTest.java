package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.matcher.Matcher;
import org.jgll.regex.matcher.MatcherFactory;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test1() {
		RegularExpression regex = Star.from(Character.from('a'));
		Automaton automaton = regex.getAutomaton();
		Visualization.generateAutomatonGraph("/Users/aliafroozeh/output", automaton);
				
		assertEquals(4, automaton.getCountStates());
		
		Matcher matcher = MatcherFactory.getMatcher(regex);
		
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

		Matcher matcher = MatcherFactory.getMatcher(regex);
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(5, matcher.match(Input.fromString("aaaaa"), 0));
	}
	
	
	@Test
	public void test3() {
		// ([a-z]+ | [(-)] | "*")*
		Alt<CharacterRange> c1 = Alt.from(CharacterRange.in('a', 'z'));
		Alt<CharacterRange> c2 = Alt.from(CharacterRange.in('(', ')'));
		Character c3 = Character.from('*');
		
		RegularExpression regex = Alt.from(Plus.from(c1), c2, c3);
	}
	

}