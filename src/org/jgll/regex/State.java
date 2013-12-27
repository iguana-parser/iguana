package org.jgll.regex;

import java.util.HashSet;
import java.util.Set;

public class State {

	private final Set<Transition> transitions;
	
	private boolean finalState;
	
	private int id;
	
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
	
	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "State" + id;
	}

}
