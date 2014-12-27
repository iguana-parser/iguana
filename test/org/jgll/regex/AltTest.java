package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.junit.Test;


public class AltTest {
	
	@Test
	public void test1() {
		Character a = Character.from('a');
		Character b = Character.from('b');
		
		RegularExpression regexp = RegexAlt.from(a, b);
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		Keyword k1 = Keyword.from("for");
		Keyword k2 = Keyword.from("forall");
		
		Automaton result = AutomatonOperations.or(k1.getAutomaton(), k2.getAutomaton());

		RunnableAutomaton dfa = result.getRunnableAutomaton();
		assertEquals(3, dfa.match(Input.fromString("for"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regexp = RegexAlt.from(Keyword.from("when"), Keyword.from("if"));

		Automaton automaton = regexp.getAutomaton();

		RunnableAutomaton dfa = automaton.getRunnableAutomaton();
		
		assertEquals(4, dfa.match(Input.fromString("when"), 0));
		assertEquals(2, dfa.match(Input.fromString("if"), 0));
	}
	
	@Test
	public void test1WithPostConditions() {
		// ([a] !>> [c] | [b]) !>> c
		RegularExpression a = new Character.Builder('a').addPreCondition(RegularExpressionCondition.notFollow(Character.from('c'))).build();
		Character b = Character.from('b');
		
		RegularExpression regexp = new RegexAlt.Builder<>(a, b).addPreCondition(RegularExpressionCondition.notFollow(Character.from('d'))).build();
		System.out.println(regexp);
		Automaton nfa = regexp.getAutomaton();
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		// TODO: fix it
//		assertEquals(-1, dfa.match(Input.fromString("ac"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		Keyword k1 = new Keyword.Builder("for").addPreCondition(RegularExpressionCondition.notFollow(Character.from(':'))).build();
		Keyword k2 = Keyword.from("forall");
		
		Automaton result = AutomatonOperations.or(k1.getAutomaton(), k2.getAutomaton());

		RunnableAutomaton dfa = result.getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("for:"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}

}
