package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.util.Input;
import org.junit.Test;

public class AltTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new RegexAlt<>(new Character('a'), new Character('b'));
		Automaton nfa = regexp.toAutomaton();
		
		assertEquals(6, nfa.getCountStates());
		
		Matcher dfa = nfa.getMatcher();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test() {
		Keyword k1 = new Keyword("for");
		Keyword k2 = new Keyword("forall");
		
		k1.addFinalStateAction(new StateAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(int length, int state) {
				System.out.println(length);
			}
		});
		
		Automaton result = AutomatonOperations.or(k1.toAutomaton(), k2.toAutomaton());
		
		Matcher dfa = result.getMatcher();
		assertEquals(3, dfa.match(Input.fromString("for"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}

	
	@Test
	public void testMatchAction() {
		RegularExpression regexp = new RegexAlt<>(new Keyword("when"), new Keyword("if"));

		final int[] l = new int[1];
		
		regexp.addFinalStateAction(new StateAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void execute(int length, int state) {
				l[0] = length;
			}
		});
		
		Automaton nfa = regexp.toAutomaton();
		
		Matcher dfa = nfa.getMatcher();
		
		assertTrue(dfa.match(Input.fromString("when")));
		assertEquals(4, l[0]);
		
		assertTrue(dfa.match(Input.fromString("if")));
		assertEquals(2, l[0]);
	}
}
