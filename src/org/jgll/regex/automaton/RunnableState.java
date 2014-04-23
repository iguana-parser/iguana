package org.jgll.regex.automaton;

import java.util.Collections;
import java.util.Set;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState implements Cloneable {
	
	private Transitions transitions;
	
	private final StateType stateType;
	
	private final Set<Action> actions;
	
	private final int id;
	
	public RunnableState(int id, StateType stateType, Set<Action> actions) {
		this.stateType = stateType;
		this.actions = actions;
		this.id = id;
	}
	
	public RunnableState(int id, StateType stateType) {
		this(id, stateType, Collections.<Action>emptySet());
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
	
	public StateType getStateType() {
		return stateType;
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
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "State" + id;
	}
	
}
