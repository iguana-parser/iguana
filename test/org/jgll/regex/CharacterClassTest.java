package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.Input;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.junit.Test;

public class CharacterClassTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new CharacterClass(new Range('a', 'z'), new Range('0', '9'));
		NFA nfa = regexp.toNFA();
		
		GraphVizUtil.generateGraph(NFAToDot.toDot(nfa.getStartState()), "/Users/ali/output", "nfa", GraphVizUtil.LEFT_TO_RIGHT);
		
		DFA dfa = nfa.toDFA();
//		assertEquals(2, nfa.getCountStates());
//		assertTrue(dfa.match(Input.fromString("a")));
//		assertEquals(1, dfa.run(Input.fromString("a"), 0));
	}
	
}
