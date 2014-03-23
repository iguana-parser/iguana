package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new RegexStar(new Character('a'));
		Automaton nfa = regexp.toAutomaton();
				
		assertEquals(4, nfa.getCountStates());
		
		Matcher matcher = nfa.getMatcher();
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(2, matcher.match(Input.fromString("aa"), 0));
		assertEquals(3, matcher.match(Input.fromString("aaa"), 0));
		assertEquals(6, matcher.match(Input.fromString("aaaaaa"), 0));
		assertEquals(17, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));
		
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
	}
	
	
	@Test
	public void test2() {
		RegularExpression regexp = new RegexStar(new Sequence<>(new RegexPlus(new CharacterClass(new Range('a', 'a')))));
		Automaton nfa = regexp.toAutomaton();
		
		Matcher matcher = nfa.getMatcher();
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(5, matcher.match(Input.fromString("aaaaa"), 0));
	}
 
}
