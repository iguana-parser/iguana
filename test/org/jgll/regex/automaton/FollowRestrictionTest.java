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
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

public class FollowRestrictionTest {
	
	RegularExpression id = RegularExpressionExamples.getId();
	
	@Test
	public void test1() {
		
		// id !>> [:]
		RegularExpression r1 = id .addCondition(RegularExpressionCondition.notFollow(new Keyword(":")))
				.addCondition(RegularExpressionCondition.notMatch(new Keyword("set")));
		
		Matcher matcher = r1.toAutomaton().getMatcher();
		
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
		Matcher matcher = r2.toAutomaton().getMatcher();
		
		assertEquals(-1, matcher.match(Input.fromString("test<>"), 0));
		assertEquals(-1, matcher.match(Input.fromString("testtest<>"), 0));
		assertEquals(4, matcher.match(Input.fromString("test?"), 0));
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
	}

	@Test
	public void test3() {
		
		// id !>> [a-z]
		RegularExpression r3 = id.addCondition(RegularExpressionCondition.notFollow(new CharacterClass(Range.in('a', 'z'))));

		Matcher matcher = r3.toAutomaton().getMatcher();
		
		assertEquals(4, matcher.match(Input.fromString("test"), 0));
		assertEquals(8, matcher.match(Input.fromString("testtest)"), 0));
		assertEquals(4, matcher.match(Input.fromString("test "), 0));
	}

	@Test
	public void test4() {
		RegularExpression r1 = new Character('a').addCondition(RegularExpressionCondition.notFollow(new Character('a')));
		RegularExpression r2 = new Character('b').addCondition(RegularExpressionCondition.notFollow(new Character('a')));
		
		RegexStar star = new RegexStar(new RegexAlt<>(r1, r2));
		Visualization.generateAutomatonGraph("/Users/aliafroozeh/output", star.toAutomaton().determinize().getStartState());
		Matcher m = star.toAutomaton().getMatcher();
		
		System.out.println(m.match(Input.fromString("ab"), 0));
	}
	
}
