package org.jgll.regex;

import java.util.BitSet;
import java.util.Set;


public class NFA implements Automaton {
	
	private final State startState;
	
	private final int[] intervals;
	
	private final State[] states;

	public NFA(State startState) {
		this.startState = startState;
		this.intervals = AutomatonOperations.getIntervals(this);
		AutomatonOperations.setStateIDs(this);
		
		Set<State> set = AutomatonOperations.getAllStates(this);
		this.states = new State[set.size()];
		
		for(State s : set) {
			states[s.getId()]  = s;
		}
		
	}
	
	@Override
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getFinalStates() {
		return AutomatonOperations.getFinalStates(this);
	}
	
	@Override
	public int getCountStates() {
		return states.length;
	}
	
	public State[] getAllStates() {
		return states;
	}
	
	public State getState(int id) {
		return states[id];
	}
	
	/**
	 * All characters accepted by this NFA.
	 */
	public BitSet getCharacters() {
		return AutomatonOperations.getCharacters(this);
	}
	
	public int[] getIntervals() {
		return intervals;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof NFA)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	public DFA toDFA() {
		NFA deterministicFA = AutomatonOperations.makeDeterministic(this);
		NFA minimizedDFA = AutomatonOperations.minimize(deterministicFA);
		return AutomatonOperations.createDFA(minimizedDFA);
	}
	
	public String toJavaCode() {
		return AutomatonOperations.toJavaCode(this);
	}
	
}
