package org.jgll.regex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.util.Input;
import org.junit.Test;


public class ExamplesTest {
	
	@Test
	public void testId() {
		Automaton nfa = RegularExpressionExamples.getId().toNFA();
		
		Matcher dfa = nfa.getMatcher();

		assertTrue(dfa.match(Input.fromString("a")));
		assertFalse(dfa.match(Input.fromString("9")));
		assertTrue(dfa.match(Input.fromString("abc")));
		assertTrue(dfa.match(Input.fromString("Identifier")));
		assertTrue(dfa.match(Input.fromString("Identifier12")));
		assertTrue(dfa.match(Input.fromString("Identifier12Assdfd")));
	}
	
	@Test
	public void testIntersectionKeywordId() {
		Automaton idAutomaton = RegularExpressionExamples.getId().toNFA().determinize();
		Automaton forAutomaton = new Keyword("for").toNFA().determinize();
		
		assertFalse(idAutomaton.intersection(forAutomaton).isLanguageEmpty());
	}
	
	@Test
	public void testFloat() {
		Automaton nfa = RegularExpressionExamples.getFloat().toNFA();
		
		Matcher dfa = nfa.getMatcher();

		assertTrue(dfa.match(Input.fromString("1.2")));
		assertTrue(dfa.match(Input.fromString("1.2"), 0, 3));
		assertFalse(dfa.match(Input.fromString("9")));
		assertFalse(dfa.match(Input.fromString(".9")));
		assertFalse(dfa.match(Input.fromString("123.")));
		assertTrue(dfa.match(Input.fromString("12.2")));
		assertTrue(dfa.match(Input.fromString("1342343.27890")));
		assertTrue(dfa.match(Input.fromString("908397439483.278902433")));
	}
	
}
