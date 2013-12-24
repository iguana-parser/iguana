package org.jgll.regex;


public class NFA {
	
	private State startState;
	private State endState;

	public NFA(State startState, State endState) {
		this.startState = startState;
		this.endState = endState;
	}
	
	public State getStartState() {
		return startState;
	}
	
	public State getEndState() {
		return endState;
	}

}
