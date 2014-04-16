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
	private Keyword k1 = new Keyword("if");
	private Keyword k2 = new Keyword("when");
	private Keyword k3 = new Keyword("new");

	@Test
	public void test1() {		
		Automaton a = difference(id.toAutomaton(), k1.toAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if:"), 0));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first:"), 0));
	}
	
	@Test
	public void test2() {
		Automaton a = difference(id.toAutomaton(), union(k1.toAutomaton(), k2.toAutomaton()));
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
	}
	
	@Test
	public void test3() {
		RegexAlt<Keyword> alt = new RegexAlt<>(k1, k2, k3);
		
		Automaton a = difference(id.toAutomaton(), alt.toAutomaton());
		assertEquals(5, a.getRunnableAutomaton().match(Input.fromString("first"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("if"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("when"), 0));
		assertEquals(-1, a.getRunnableAutomaton().match(Input.fromString("new"), 0));
	}

}
