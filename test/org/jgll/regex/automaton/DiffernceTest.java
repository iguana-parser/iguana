package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonOperations.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.RegularExpressionExamples;
import org.jgll.util.Input;
import org.junit.Test;

public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Keyword k1 = Keyword.from("if");
	private Keyword k2 = Keyword.from("when");
	private Keyword k3 = Keyword.from("new");

	@Test
	public void test1() {		
		Automaton a = difference(id.getAutomaton(), k1.getAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if:"), 0));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first:"), 0));
	}
	
	@Test
	public void test2() {
		Automaton a = difference(id.getAutomaton(), union(k1.getAutomaton(), k2.getAutomaton()));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
	}
	
	@Test
	public void test3() {
		RegexAlt<Keyword> alt = RegexAlt.from(k1, k2, k3);
		
		Automaton a = difference(id.getAutomaton(), alt.getAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("new"), 0));
	}

}
