package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;

public class StarTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = RegexStar.from(Character.from('a'));
		Automaton nfa = regexp.getAutomaton();
				
		assertEquals(4, nfa.getCountStates());
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		
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
		RegularExpression regexp = RegexStar.from(Sequence.from(RegexPlus.from(CharacterClass.from(Range.in('a', 'a')))));
		Automaton nfa = regexp.getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(5, matcher.match(Input.fromString("aaaaa"), 0));
	}
	
	
	@Test
	public void test3() {
		// ([a-z]+ | [(-)] | "*")*
		CharacterClass c1 = CharacterClass.from(Range.in('a', 'z'));
		CharacterClass c2 = CharacterClass.from(Range.in('(', ')'));
		Character c3 = Character.from('*');
		
		RegularExpression regex = RegexAlt.from(RegexPlus.from(c1), c2, c3);
	}
	
	@Test
	public void test1WithPreConditions() {
		RegexStar regexp = new RegexStar.Builder(Character.from('a')).addCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Automaton nfa = regexp.getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString(":"), 0));
		assertEquals(0, matcher.match(Input.fromString(""), 0));

		assertEquals(-1, matcher.match(Input.fromString("a:"), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));

		assertEquals(-1, matcher.match(Input.fromString("aa:"), 0));
		assertEquals(2, matcher.match(Input.fromString("aa"), 0));

		assertEquals(-1, matcher.match(Input.fromString("aaa:"), 0));
		assertEquals(3, matcher.match(Input.fromString("aaa"), 0));

		assertEquals(-1, matcher.match(Input.fromString("aaaaaa:"), 0));
		assertEquals(6, matcher.match(Input.fromString("aaaaaa"), 0));

		assertEquals(-1, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa:"), 0));
		assertEquals(17, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));

		assertEquals(-1, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:"), 0));
		assertEquals(33, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));
	}
 
}