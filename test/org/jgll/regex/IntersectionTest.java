package org.jgll.regex;

import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.junit.Test;

public class IntersectionTest {

	@Test
	public void test() {
		
		State x0 = new State();
		State x1 = new State(true);
		
		x0.addTransition(new Transition('0', x0));
		x0.addTransition(new Transition('1', x1));
		x1.addTransition(new Transition('0', x1));
		x1.addTransition(new Transition('1', x0));
		
		Automaton a1 = new Automaton(x0);
		
		
		State y0 = new State(true);
		State y1 = new State();
		
		y0.addTransition(new Transition('0', y1));
		y0.addTransition(new Transition('1', y1));
		y1.addTransition(new Transition('0', y0));
		y1.addTransition(new Transition('1', y0));
		
		Automaton a2 = new Automaton(y0);
		
		Automaton a = AutomatonOperations.product(a1, a2);
		
		GraphVizUtil.generateGraph(NFAToDot.toDot(a.getStartState()), "/Users/aliafroozeh/output", "nfa", GraphVizUtil.LEFT_TO_RIGHT);
	}
}
