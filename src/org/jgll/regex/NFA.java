package org.jgll.regex;

import java.util.BitSet;
import java.util.Set;

import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;


public class NFA implements Automaton {
	
	private final State startState;
	
	private final int[] intervals;
	
	private final Set<State> states;

	public NFA(State startState) {
		this.startState = startState;
		this.intervals = AutomatonOperations.getIntervals(this);
		this.states = AutomatonOperations.getAllStates(this);
		AutomatonOperations.setStateIDs(this);
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
		return states.size();
	}
	
	public Set<State> getAllStates() {
		return states;
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
		AutomatonOperations.minimize(deterministicFA);
		return AutomatonOperations.createDFA(deterministicFA);
	}
	
	public String toJavaCode() {
		return AutomatonOperations.toJavaCode(this);
	}
	
}
