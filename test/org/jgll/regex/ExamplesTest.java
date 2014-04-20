package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;


public class ExamplesTest {
	
	@Test
	public void testId() {
		Automaton nfa = RegularExpressionExamples.getId().getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();

		assertTrue(matcher.match(Input.fromString("a")));
		assertFalse(matcher.match(Input.fromString("9")));
		assertTrue(matcher.match(Input.fromString("abc")));
		assertTrue(matcher.match(Input.fromString("Identifier")));
		assertTrue(matcher.match(Input.fromString("Identifier12")));
		assertTrue(matcher.match(Input.fromString("Identifier12Assdfd")));
	}
	
	@Test
	public void testIntersectionKeywordId() {
		Automaton idAutomaton = RegularExpressionExamples.getId().getAutomaton().determinize();
		Automaton forAutomaton = new Keyword("for").getAutomaton().determinize();
		
		assertFalse(idAutomaton.intersection(forAutomaton).isLanguageEmpty());
	}
	
	@Test
	public void testFloat() {
		Automaton nfa = RegularExpressionExamples.getFloat().getAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();

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
		Automaton nfa = RegularExpressionExamples.getJavaUnicodeEscape().getAutomaton();
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertTrue(dfa.match(Input.fromString("\\u0123")));		
	}
	
	@Test
	public void testCharacter() {
		Automaton nfa = RegularExpressionExamples.getCharacter().getAutomaton();
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertTrue(matcher.match(Input.fromString("'ab'")));
	}
	
	@Test
	public void testStringPart() {
		Automaton a = RegularExpressionExamples.getStringPart().getAutomaton();
		RunnableAutomaton matcher = a.getRunnableAutomaton();
		
		assertTrue(matcher.match(Input.fromString("abcd")));
		assertFalse(matcher.match(Input.fromString("\\aa")));
		assertFalse(matcher.match(Input.fromString("\"aaa")));
	}
	
	// TODO: fix it
//	@Test
//	public void shortestVsLongestMatch() {
//		Automaton  a = RegularExpressionExamples.getId().toAutomaton();
//		RunnableState matcher = a.getRunnableAutomaton();
//		
//		assertEquals(8, matcher.match(Input.fromString("Variable"), 0));
//		
//		matcher = matcher.setMode(Matcher.SHORTEST_MATCH);
//		assertEquals(1, matcher.match(Input.fromString("Variable"), 0));
//	}
	
}
