package org.jgll.regex;

import org.junit.Test;

public class MinimizationTest {
	
	@Test
	public void test() {
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
		
		NFA minimize = AutomatonOperations.minimize(nfa);
	}

}
