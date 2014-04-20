package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;


public class AltTest {
	
	@Test
	public void test1() {
		Character a = new Character('a');
		Character b = new Character('b');
		
		RegularExpression regexp = new RegexAlt<>(a, b);
		Automaton nfa = regexp.getAutomaton();
		
		assertEquals(6, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		Keyword k1 = new Keyword("for");
		Keyword k2 = new Keyword("forall");
		
		Automaton result = AutomatonOperations.or(k1.getAutomaton(), k2.getAutomaton());

		RunnableAutomaton dfa = result.getRunnableAutomaton();
		assertEquals(3, dfa.match(Input.fromString("for"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regexp = new RegexAlt<>(new Keyword("when"), new Keyword("if"));

		Automaton automaton = regexp.getAutomaton();

		RunnableAutomaton dfa = automaton.getRunnableAutomaton();
		
		assertEquals(4, dfa.match(Input.fromString("when"), 0));
		assertEquals(2, dfa.match(Input.fromString("if"), 0));
	}
	
	@Test
	public void test1WithPostConditions() {
		// ([a] !>> [c] | [b]) !>> c
		RegularExpression a = new Character('a').withCondition(RegularExpressionCondition.notFollow(new Character('c')));
		Character b = new Character('b');
		
		RegularExpression regexp = new RegexAlt<>(a, b).withCondition(RegularExpressionCondition.notFollow(new Character('d')));
		System.out.println(regexp);
		Automaton nfa = regexp.getAutomaton();
		Visualization.generateAutomatonGraph("/Users/ali/output", nfa.getStartState());
		
		assertEquals(6, nfa.getCountStates());
		
		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("ac"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2WithPostConditions() {
		Keyword k1 = (Keyword) new Keyword("for").withCondition(RegularExpressionCondition.notFollow(new Character(':')));
		Keyword k2 = new Keyword("forall");
		
		Automaton result = AutomatonOperations.or(k1.getAutomaton(), k2.getAutomaton());

		RunnableAutomaton dfa = result.getRunnableAutomaton();
		assertEquals(-1, dfa.match(Input.fromString("for:"), 0));
		assertEquals(6, dfa.match(Input.fromString("forall"), 0));
	}

}
