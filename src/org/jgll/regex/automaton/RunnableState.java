package org.jgll.regex.automaton;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState {
	
	private Transitions transitions;
	private boolean finalState;
	
	public RunnableState(boolean finalState) {
		this.finalState = finalState;
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
	
}
