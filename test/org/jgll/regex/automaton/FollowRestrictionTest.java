package org.jgll.regex.automaton;

import static org.junit.Assert.assertEquals;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

public class FollowRestrictionTest {
	
	private Matcher matcher1;
	private Matcher matcher2;
	private Matcher matcher3;

	@Before
	public void init() {
		// id !>> [:]
		RegularExpression id = RegularExpressionExamples.getId();
		RegularExpression r1 = id.addCondition(RegularExpressionCondition.notFollow(new Keyword(":")));
		matcher1 = r1.toAutomaton().getMatcher();
		
		// id !>> "<>"
		RegularExpression r2 = id.addCondition(RegularExpressionCondition.notFollow(new Keyword("<>")));
		matcher2 = r2.toAutomaton().getMatcher();
		
		// id !>> [a-z]
		RegularExpression r3 = id.addCondition(RegularExpressionCondition.notFollow(new CharacterClass(Range.in('a', 'z'))));
		matcher3 = r3.toAutomaton().getMatcher();
	}
	
	@Test
	public void test1() {
		assertEquals(-1, matcher1.match(Input.fromString("test:"), 0));
		assertEquals(-1, matcher1.match(Input.fromString("testtest:"), 0));
		assertEquals(4, matcher1.match(Input.fromString("test?"), 0));
	}
	
	@Test
	public void test2() {
		assertEquals(-1, matcher2.match(Input.fromString("test<>"), 0));
		assertEquals(-1, matcher2.match(Input.fromString("testtest<>"), 0));
		assertEquals(4, matcher2.match(Input.fromString("test?"), 0));
	}

	@Test
	public void test3() {
		assertEquals(4, matcher3.match(Input.fromString("test"), 0));
		assertEquals(8, matcher3.match(Input.fromString("testtest)"), 0));
		assertEquals(4, matcher3.match(Input.fromString("test "), 0));
	}

	
	
}
