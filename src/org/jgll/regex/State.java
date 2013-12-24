package org.jgll.regex;

import java.util.HashSet;
import java.util.Set;

public class State {

	private final Set<Transition> transitions;
	
	private final boolean finalState;
	
	public State() {
		this(false);
	}
	
	public State(boolean finalState) {
		this.transitions = new HashSet<>();
		this.finalState = finalState;
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
	}

}
