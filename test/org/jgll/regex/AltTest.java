package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.EOF;
import org.jgll.util.Input;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.junit.Test;

public class AltTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new RegexAlt<>(new Character('a'), new Character('b'));
		Automaton nfa = regexp.toAutomaton();
		
		GraphVizUtil.generateGraph(NFAToDot.toDot(nfa.getStartState()), "/Users/aliafroozeh/output", "nfa", GraphVizUtil.LEFT_TO_RIGHT);
		
		assertEquals(6, nfa.getCountStates());
		
		Matcher dfa = nfa.getMatcher();
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	

	@Test
	public void test2() {
		Character a = new Character('a');
		Character b = new Character('b');
		RegularExpression regexp = new RegexAlt<>(a, b, EOF.getInstance());
		Automaton nfa = regexp.toAutomaton().minimize();

		Matcher dfa = nfa.getMatcher();
		assertTrue(dfa.match(Input.fromString("a")));
		assertTrue(dfa.match(Input.fromString("b")));
		assertTrue(dfa.match(Input.fromString("")));
	}

}