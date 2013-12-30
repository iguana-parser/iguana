package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.util.Input;
import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void test1() {
		RegularExpression regexp = new Sequence(new Character('a'), new Character('b'));
		NFA nfa = regexp.toNFA();

//		GraphVizUtil.generateGraph(NFAToDot.toDot(nfa.getStartState()), "/Users/ali/output", "nfa", GraphVizUtil.LEFT_TO_RIGHT);
		
		assertEquals(6, nfa.getCountStates());

		DFA dfa = nfa.toDFA();
		
		assertTrue(dfa.match(Input.fromString("ab")));
	}
	
}
