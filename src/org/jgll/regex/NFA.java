package org.jgll.regex;

import java.util.Set;

import org.jgll.util.CollectionsUtil;

public class NFA {
	
	private State startState;
	private Set<State> endStates;

	public NFA(State startState, Set<State> endStates) {
		this.startState = startState;
		this.endStates = endStates;
	}
	
	public NFA(State startState, State...endStates) {
		this(startState, CollectionsUtil.set(endStates));
	}
	
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getEndStates() {
		return endStates;
	}

}
