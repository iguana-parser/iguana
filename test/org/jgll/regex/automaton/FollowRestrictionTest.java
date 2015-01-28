package org.jgll.regex.automaton;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.SymbolBuilder;
import org.jgll.regex.Alt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.util.Input;
import org.junit.Test;

public class FollowRestrictionTest {
	
	SymbolBuilder<? extends RegularExpression> idBuilder = RegularExpressionExamples.getId();
	
	@Test
	public void test1() {
		
		// id !>> [:]
		RegularExpression r1 = idBuilder.addPostCondition(RegularExpressionCondition.notFollow(Character.from(':')))				
								 .addPreCondition(RegularExpressionCondition.notMatch(Sequence.from("set"))).build();
		
		
		RunnableAutomaton matcher = r1.getAutomaton().getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString("test:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("testtest:"), 0));
		assertEquals(4, matcher.match(Input.fromString("test?"), 0));
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
		assertEquals(-1, matcher.match(Input.fromString("set"), 0));
	}
	
	@Test
	public void test2() {
		
		// id !>> "<>"
		RegularExpression r2 = idBuilder.addPreCondition(RegularExpressionCondition.notFollow(Sequence.from("<>"))).build();
		RunnableAutomaton matcher = r2.getAutomaton().getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString("test<>"), 0));
		assertEquals(-1, matcher.match(Input.fromString("testtest<>"), 0));
		assertEquals(4, matcher.match(Input.fromString("test?"), 0));
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
	}

	@Test
	public void test3() {
		
		// id !>> [a-z]
		RegularExpression r3 = idBuilder.addPreCondition(RegularExpressionCondition.notFollow(Alt.from(CharacterRange.in('a', 'z')))).build();

		RunnableAutomaton matcher = r3.getAutomaton().getRunnableAutomaton();
		
//		assertEquals(4, matcher.match(Input.fromString("test"), 0));
//		assertEquals(8, matcher.match(Input.fromString("testtest)"), 0));
//		assertEquals(4, matcher.match(Input.fromString("test "), 0));
	}

	@Test
	public void test4() {
		// (![*] | [*] !>> [/])*
		Alt<CharacterRange> r1 = Alt.not(Character.from('*'));
		Character r2 = new Character.Builder('*').addPreCondition(RegularExpressionCondition.notFollow(Character.from('/'))).build();
		Star star = Star.from(Alt.from(r1, r2));
		
		RunnableAutomaton m = star.getAutomaton().getRunnableAutomaton();
		
		assertEquals(2, m.match(Input.fromString("ab"), 0));
		assertEquals(-1, m.match(Input.fromString("*/"), 0));
		assertEquals(7, m.match(Input.fromString("/*aaa**"), 0));
	}
	
}
