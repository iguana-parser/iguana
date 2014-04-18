package org.jgll.regex.automaton;

import static org.junit.Assert.assertEquals;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegexStar;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.util.Input;
import org.junit.Test;

public class FollowRestrictionTest {
	
	RegularExpression id = RegularExpressionExamples.getId();
	
	@Test
	public void test1() {
		
		// id !>> [:]
		RegularExpression r1 = id.addCondition(RegularExpressionCondition.notFollow(new Character(':')))				
								 .addCondition(RegularExpressionCondition.notMatch(new Keyword("set")));
		
		
		RunnableAutomaton matcher = r1.toAutomaton().getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString("test:"), 0));
		assertEquals(-1, matcher.match(Input.fromString("testtest:"), 0));
		assertEquals(4, matcher.match(Input.fromString("test?"), 0));
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
		assertEquals(-1, matcher.match(Input.fromString("set"), 0));
	}
	
	@Test
	public void test2() {
		
		// id !>> "<>"
		RegularExpression r2 = id.addCondition(RegularExpressionCondition.notFollow(new Keyword("<>")));
		RunnableAutomaton matcher = r2.toAutomaton().getRunnableAutomaton();
		
		assertEquals(-1, matcher.match(Input.fromString("test<>"), 0));
		assertEquals(-1, matcher.match(Input.fromString("testtest<>"), 0));
		assertEquals(4, matcher.match(Input.fromString("test?"), 0));
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
	}

	@Test
	public void test3() {
		
		// id !>> [a-z]
		RegularExpression r3 = id.addCondition(RegularExpressionCondition.notFollow(new CharacterClass(Range.in('a', 'z'))));

		RunnableAutomaton matcher = r3.toAutomaton().getRunnableAutomaton();
		
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
		assertEquals(8, matcher.match(Input.fromString("testtest)"), 0));
		assertEquals(4, matcher.match(Input.fromString("test "), 0));
	}

	@Test
	public void test4() {
		// (![*] | [*] !>> [/])*
		RegularExpression r1 = new Character('*').not();
		RegularExpression r2 = new Character('*').addCondition(RegularExpressionCondition.notFollow(new Character('/')));
		RegexStar star = new RegexStar(new RegexAlt<>(r1, r2));
		
		RunnableAutomaton m = star.toAutomaton().getRunnableAutomaton();
		
		assertEquals(2, m.match(Input.fromString("ab"), 0));
		assertEquals(-1, m.match(Input.fromString("*/"), 0));
		assertEquals(7, m.match(Input.fromString("/*aaa**"), 0));
	}
	
}
