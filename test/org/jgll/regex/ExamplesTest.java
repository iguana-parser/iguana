package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.matcher.DFAMatcher;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;
import org.junit.Test;


public class ExamplesTest {
	
	@Test
	public void testId() {
		RegularExpression id = RegularExpressionExamples.getId();
		Automaton automaton = id.getAutomaton();
		
		assertEquals(15, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(6, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		
		assertTrue(matcher.match(Input.fromString("a")));
		assertFalse(matcher.match(Input.fromString("9")));
		assertTrue(matcher.match(Input.fromString("abc")));
		assertTrue(matcher.match(Input.fromString("Identifier")));
		assertTrue(matcher.match(Input.fromString("Identifier12")));
		assertTrue(matcher.match(Input.fromString("Identifier12Assdfd")));
	}
	
	@Test
	public void testIntersectionKeywordId() {
//		Automaton idAutomaton = RegularExpressionExamples.getId().build().getAutomaton().determinize();
//		Automaton forAutomaton = Sequence.from("for").getAutomaton().determinize();
//		
//		assertFalse(idAutomaton.intersection(forAutomaton).isLanguageEmpty());
	}
	
	@Test
	public void testFloat() {
		RegularExpression _float = RegularExpressionExamples.getFloat();
		
		Automaton automaton = _float.getAutomaton();
		assertEquals(10, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(6, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);

		assertTrue(matcher.match(Input.fromString("1.2")));
		assertTrue(matcher.match(Input.fromString("1.2"), 0, 3));
		assertFalse(matcher.match(Input.fromString("9")));
		assertFalse(matcher.match(Input.fromString(".9")));
		assertFalse(matcher.match(Input.fromString("123.")));
		assertTrue(matcher.match(Input.fromString("12.2")));
		assertTrue(matcher.match(Input.fromString("1342343.27890")));
		assertTrue(matcher.match(Input.fromString("908397439483.278902433")));
	}
	
	@Test
	public void testJavaUnicodeEscape() {
		RegularExpression regex = RegularExpressionExamples.getJavaUnicodeEscape();
		Automaton automaton = regex.getAutomaton();
		assertEquals(34, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(37, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		assertTrue(matcher.match(Input.fromString("\\u0123")));
	}
	
	@Test
	public void testCharacter() {
		RegularExpression regex = RegularExpressionExamples.getCharacter();
		Automaton automaton = regex.getAutomaton();
		assertEquals(15, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(7, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		assertTrue(matcher.match(Input.fromString("'ab'")));
	}
	
	@Test
	public void testStringPart() {
		RegularExpression regex = RegularExpressionExamples.getStringPart();
		Automaton automaton = regex.getAutomaton();
		assertEquals(18, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(7, automaton.getCountStates());
		
		Matcher matcher = new DFAMatcher(automaton);
		
		assertTrue(matcher.match(Input.fromString("abcd")));
		assertFalse(matcher.match(Input.fromString("\\aa")));
		assertFalse(matcher.match(Input.fromString("\"aaa")));
	}
	
	@Test
	public void testMultilineComment() {
		Automaton a = RegularExpressionExamples.getMultilineComment().getAutomaton();
		
//		assertTrue(matcher.match(Input.fromString("/*a*/")));
	}	
}
