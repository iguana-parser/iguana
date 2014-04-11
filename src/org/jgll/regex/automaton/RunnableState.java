package org.jgll.regex.automaton;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState {
	
	private Transitions transitions;
	
	private boolean finalState;
	
	private boolean rejectState;
	
	public RunnableState(boolean finalState, boolean rejectState) {
		this.finalState = finalState;
		this.rejectState = rejectState;
	}
	
	public void setTransitions(Transitions transitions) {
		this.transitions = transitions;
	}

	public Transitions getTransitions() {
		return transitions;
	}
	
	public RunnableState move(Input input, int inputIndex) {
		return transitions.move(input, inputIndex);
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public boolean isRejectState() {
		return rejectState;
	}
	
}
