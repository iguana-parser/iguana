package org.jgll.regex;

import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.util.Input;
import org.junit.Test;

public class ReverseAutomatonTest {
	
	@Test
	public void test() {
		RegularExpression r = new Keyword("test");
		Automaton a = r.toNFA().reverse();
		assertTrue(a.getMatcher().match(Input.fromString("tset")));
	}

}
