package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;
import org.junit.Test;


public class ExamplesTest {
	
	@Test
	public void testId() {
		RegularExpression id = RegularExpressionExamples.getId().build();
		
		Matcher matcher = id.getMatcher();
		Matcher matcher2 = id.getJavaRegexMatcher();
		
		assertTrue(matcher.match(Input.fromString("a")));
		assertFalse(matcher.match(Input.fromString("9")));
		assertTrue(matcher.match(Input.fromString("abc")));
		assertTrue(matcher.match(Input.fromString("Identifier")));
		assertTrue(matcher.match(Input.fromString("Identifier12")));
		assertTrue(matcher.match(Input.fromString("Identifier12Assdfd")));
		
		assertTrue(matcher2.match(Input.fromString("a")));
		assertFalse(matcher2.match(Input.fromString("9")));
		assertTrue(matcher2.match(Input.fromString("abc")));
		assertTrue(matcher2.match(Input.fromString("Identifier")));
		assertTrue(matcher2.match(Input.fromString("Identifier12")));
		assertTrue(matcher2.match(Input.fromString("Identifier12Assdfd")));
	}
	
	@Test
	public void testIntersectionKeywordId() {
		Automaton idAutomaton = RegularExpressionExamples.getId().build().getAutomaton().determinize();
		Automaton forAutomaton = Sequence.from("for").getAutomaton().determinize();
		
		assertFalse(idAutomaton.intersection(forAutomaton).isLanguageEmpty());
	}
	
	@Test
	public void testFloat() {
		RegularExpression _float = RegularExpressionExamples.getFloat().build();
		
		Matcher matcher = _float.getMatcher();
		Matcher matcher2 = _float.getJavaRegexMatcher();

		assertTrue(matcher.match(Input.fromString("1.2")));
		assertTrue(matcher.match(Input.fromString("1.2"), 0, 3));
		assertFalse(matcher.match(Input.fromString("9")));
		assertFalse(matcher.match(Input.fromString(".9")));
		assertFalse(matcher.match(Input.fromString("123.")));
		assertTrue(matcher.match(Input.fromString("12.2")));
		assertTrue(matcher.match(Input.fromString("1342343.27890")));
		assertTrue(matcher.match(Input.fromString("908397439483.278902433")));
		
		assertTrue(matcher2.match(Input.fromString("1.2")));
		assertTrue(matcher2.match(Input.fromString("1.2"), 0, 3));
		assertFalse(matcher2.match(Input.fromString("9")));
		assertFalse(matcher2.match(Input.fromString(".9")));
		assertFalse(matcher2.match(Input.fromString("123.")));
		assertTrue(matcher2.match(Input.fromString("12.2")));
		assertTrue(matcher2.match(Input.fromString("1342343.27890")));
		assertTrue(matcher2.match(Input.fromString("908397439483.278902433")));
	}
	
	@Test
	public void testJavaUnicodeEscape() {
		Automaton nfa = RegularExpressionExamples.getJavaUnicodeEscape().build().getAutomaton();
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("\\u0123")));
	}
	
	@Test
	public void testCharacter() {
		Automaton nfa = RegularExpressionExamples.getCharacter().build().getAutomaton();
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertTrue(matcher.match(Input.fromString("'ab'")));
	}
	
	@Test
	public void testStringPart() {
		Automaton a = RegularExpressionExamples.getStringPart().build().getAutomaton();
		RunnableAutomaton matcher = a.getRunnableAutomaton();
		
		assertTrue(matcher.match(Input.fromString("abcd")));
		assertFalse(matcher.match(Input.fromString("\\aa")));
		assertFalse(matcher.match(Input.fromString("\"aaa")));
	}
	
	@Test
	public void testMultilineComment() {
		Automaton a = RegularExpressionExamples.getMultilineComment().build().getAutomaton();
		
		RunnableAutomaton matcher = a.getRunnableAutomaton();
		
//		assertTrue(matcher.match(Input.fromString("/*a*/")));
	}	
}
