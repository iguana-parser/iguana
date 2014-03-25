package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;


public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Keyword k1 = new Keyword("if");
	private Keyword k2 = new Keyword("when");
	private Keyword k3 = new Keyword("new");

	@Test
	public void test1() {
		Automaton a = AutomatonOperations.intersection(id.toAutomaton(), k1.toAutomaton());
		Visualization.generateAutomatonGraph("/Users/ali/output", a.getStartState());
		assertTrue(a.getMatcher().match(Input.fromString("first")));
	}

}
