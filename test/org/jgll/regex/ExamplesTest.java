package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.util.Input;
import org.junit.Test;


public class ExamplesTest {
	
	@Test
	public void testId() {
		Automaton nfa = RegularExpressionExamples.getId().toAutomaton();
		
		Matcher matcher = nfa.getMatcher();

		assertTrue(matcher.match(Input.fromString("a")));
		assertFalse(matcher.match(Input.fromString("9")));
		assertTrue(matcher.match(Input.fromString("abc")));
		assertTrue(matcher.match(Input.fromString("Identifier")));
		assertTrue(matcher.match(Input.fromString("Identifier12")));
		assertTrue(matcher.match(Input.fromString("Identifier12Assdfd")));
	}
	
	@Test
	public void testIntersectionKeywordId() {
		Automaton idAutomaton = RegularExpressionExamples.getId().toAutomaton().determinize();
		Automaton forAutomaton = new Keyword("for").toAutomaton().determinize();
		
		assertFalse(idAutomaton.intersection(forAutomaton).isLanguageEmpty());
	}
	
	@Test
	public void testFloat() {
		Automaton nfa = RegularExpressionExamples.getFloat().toAutomaton();
		
		Matcher matcher = nfa.getMatcher();

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
		Automaton nfa = RegularExpressionExamples.getJavaUnicodeEscape().toAutomaton();
		Matcher dfa = nfa.getMatcher();
		assertTrue(dfa.match(Input.fromString("\\u0123")));		
	}
	
	@Test
	public void testCharacter() {
		Automaton nfa = RegularExpressionExamples.getCharacter().toAutomaton();
		Matcher matcher = nfa.getMatcher();
		
		assertTrue(matcher.match(Input.fromString("'ab'")));
	}
	
	@Test
	public void testStringPart() {
		Automaton a = RegularExpressionExamples.getStringPart().toAutomaton();
		Matcher matcher = a.getMatcher();
		
		assertTrue(matcher.match(Input.fromString("abcd")));
		assertFalse(matcher.match(Input.fromString("\\aa")));
		assertFalse(matcher.match(Input.fromString("\"aaa")));
	}
	
	@Test
	public void shortestVsLongestMatch() {
		Automaton  a = RegularExpressionExamples.getId().toAutomaton();
		Matcher matcher = a.getMatcher();
		
		assertEquals(8, matcher.match(Input.fromString("Variable"), 0));
		
		matcher = matcher.setMode(Matcher.SHORTEST_MATCH);
		assertEquals(1, matcher.match(Input.fromString("Variable"), 0));
	}
	
}
