package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.junit.Test;

public class MinimizationTest {
	
	@Test
	public void test1() {
		State q0 = new State();
		State q1 = new State();
		State q2 = new State();
		State q3 = new State();
		State q4 = new State();
		State q5 = new State(true);
		State q6 = new State(true);
		State q7 = new State();
		
		q0.addTransition(new Transition('0', q7));
		q0.addTransition(new Transition('1', q1));
		
		q1.addTransition(new Transition('0', q7));
		q1.addTransition(new Transition('1', q0));

		q2.addTransition(new Transition('0', q4));
		q2.addTransition(new Transition('1', q5));
		
		q3.addTransition(new Transition('0', q4));
		q3.addTransition(new Transition('1', q5));
		
		q4.addTransition(new Transition('1', q6));
		
		q5.addTransition(new Transition('0', q5));
		q5.addTransition(new Transition('1', q5));
		
		q6.addTransition(new Transition('0', q6));
		q6.addTransition(new Transition('1', q5));
		
		q7.addTransition(new Transition('0', q2));
		q7.addTransition(new Transition('1', q2));
		
		NFA nfa = new NFA(q0);
		
		NFA minimized = AutomatonOperations.minimize(nfa);
	
		assertEquals(getAutomaton1(), minimized);
	}
	
	private NFA getAutomaton1() {
		State state1 = new State();
		state1.addTransition(new Transition(49, 49, state1));
		State state2 = new State();
		State state3 = new State();
		State state4 = new State();
		State state5 = new State(true);
		state5.addTransition(new Transition(49, 49, state5));
		state5.addTransition(new Transition(48, 48, state5));
		state4.addTransition(new Transition(49, 49, state5));
		state3.addTransition(new Transition(48, 48, state4));
		state3.addTransition(new Transition(49, 49, state5));
		state2.addTransition(new Transition(48, 48, state3));
		state2.addTransition(new Transition(49, 49, state3));
		state1.addTransition(new Transition(48, 48, state2));
		
		return new NFA(state1);
	}
	
	
	@Test
	public void test2() {
		State a = new State();
		State b = new State();
		State c = new State();
		State d = new State();
		State e = new State(true);
		
		a.addTransition(new Transition('0', b));
		a.addTransition(new Transition('1', c));
		b.addTransition(new Transition('0', '1', d));
		c.addTransition(new Transition('0', '1', d));
		d.addTransition(new Transition('0', '1', e));
		
		NFA nfa = new NFA(a);
		
		NFA minimized = AutomatonOperations.minimize(nfa);
		
		assertEquals(getAutomaton2(), minimized);
	}
	
	private NFA getAutomaton2() {
		State state1 = new State();
		State state2 = new State();
		State state3 = new State();
		State state4 = new State(true);
		state3.addTransition(new Transition(48, 49, state4));
		state2.addTransition(new Transition(48, 49, state3));
		state1.addTransition(new Transition(49, 49, state2));
		state1.addTransition(new Transition(48, 48, state2));
		return new NFA(state1);
	}
	

}
