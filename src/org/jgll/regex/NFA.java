package org.jgll.regex;

import java.util.Set;

public class NFA {
	
	private State startState;
	private Set<State> endStates;

	public NFA(State startState, Set<State> endStates) {
		this.startState = startState;
		this.endStates = endStates;
	}
	
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getEndStates() {
		return endStates;
	}

}
