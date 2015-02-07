package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = Star.from(Character.from('a'));
		Automaton nfa = regexp.getAutomaton();
				
		assertEquals(4, nfa.getCountStates());
		
		Matcher matcher = regexp.getMatcher();
		
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
		RegularExpression regexp = Star.from(Sequence.from(Plus.from(Alt.from(CharacterRange.in('a', 'a')))));
		Automaton nfa = regexp.getAutomaton();
		
		Matcher matcher = regexp.getMatcher();
		
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