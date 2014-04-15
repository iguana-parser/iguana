package org.jgll.regex.automaton;

import java.util.Collections;
import java.util.Set;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState implements Cloneable {
	
	private Transitions transitions;
	
	private boolean finalState;
	
	private boolean rejectState;
	
	private Set<Action> actions;
	
	public RunnableState(boolean finalState, boolean rejectState, Set<Action> actions) {
		this.finalState = finalState;
		this.rejectState = rejectState;
		this.actions = actions;
	}
	
	public RunnableState(boolean finalState, boolean rejectState) {
		this(finalState, rejectState, Collections.<Action>emptySet());
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
	
	
	public void setRejectState(boolean rejectState) {
		this.rejectState = rejectState;
	}
	
	public Set<Action> getActions() {
		return actions;
	}
	
	public boolean executeActions(Input input, int inputIndex) {
		for (Action action : actions) {
			if (action.execute(input, inputIndex)) {
				return  true;
			}
		}
		
		return false;
	}
	
	@Override
	public RunnableState clone() {
		try {
			return (RunnableState) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException("Should not reach here!");
		}
	}
	
}
