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
		RegularExpression regexp = new RegexStar(new Character('a'));
		Automaton nfa = regexp.toAutomaton();
				
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
		RegularExpression regexp = new RegexStar(new Sequence<>(new RegexPlus(new CharacterClass(new Range('a', 'a')))));
		Automaton nfa = regexp.toAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		
		assertEquals(0, matcher.match(Input.fromString(""), 0));
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(5, matcher.match(Input.fromString("aaaaa"), 0));
	}
	
	@Test
	public void test1WithPreConditions() {
		RegularExpression regexp = new RegexStar(new Character('a')).addCondition(RegularExpressionCondition.notFollow(new Character(':')));
		Automaton nfa = regexp.toAutomaton();
		
		assertEquals(4, nfa.getCountStates());
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString(":"), 0));
		assertEquals(-1, matcher.match(Input.fromString("a:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("aa:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("aaa:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("aaaaaa:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:"), 0));
	}
 
}