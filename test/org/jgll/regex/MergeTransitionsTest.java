package org.jgll.regex;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.junit.Test;

public class MergeTransitionsTest {
	
	@Test
	public void test() {
		RegularExpression regexp = new CharacterClass(Range.in('0', '4'), Range.in('5', '7'), Range.in('8', '9'));
		Automaton a = AutomatonOperations.minimize(regexp.toNFA().determinize());
		AutomatonOperations.mergeTransitions(a);
		GraphVizUtil.generateGraph(NFAToDot.toDot(a.getStartState()), "/Users/ali/output", "m", GraphVizUtil.LEFT_TO_RIGHT);		
	}

}
