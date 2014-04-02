package org.jgll.regex.automaton;

import static org.jgll.regex.automaton.AutomatonOperations.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
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
		assertTrue(a.getMatcher().match(Input.fromString("first")));
		assertFalse(a.getMatcher().match(Input.fromString("if")));
	}
	
	@Test
	public void test2() {
		Automaton a = difference(id.toAutomaton(), union(k1.toAutomaton(), k2.toAutomaton()));
		assertTrue(a.getMatcher().match(Input.fromString("first")));
		assertFalse(a.getMatcher().match(Input.fromString("if")));
		assertFalse(a.getMatcher().match(Input.fromString("when")));
	}
	
	@Test
	public void test3() {
		Automaton a = difference(id.toAutomaton(), union(k1.toAutomaton(), union(k2.toAutomaton(), k3.toAutomaton())));
		assertTrue(a.getMatcher().match(Input.fromString("first")));
		assertFalse(a.getMatcher().match(Input.fromString("if")));
		assertFalse(a.getMatcher().match(Input.fromString("when")));
		assertFalse(a.getMatcher().match(Input.fromString("new")));
	}

}
