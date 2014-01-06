package org.jgll.regex;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
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
	
	/**
	 * Determines whether two NFAs are isomorphic. 
	 * The NFAs are first made deterministic before performing the equality check.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof NFA)) {
			return false;
		}
		
		NFA other = (NFA) obj;
		
		// Checks whether two NFAs accept the same language 
		if(!Arrays.equals(intervals, other.intervals)) {
			return false;
		}
		
		// TODO: Maybe we should change the start symbol of the automaton after it's made 
		// deterministic
		
		NFA thisNFA = AutomatonOperations.makeDeterministic(this);
		NFA otherNFA = AutomatonOperations.makeDeterministic(other);
		
		Set<State> visitedStates = new HashSet<>();
		
		return isEqual(thisNFA.getStartState(), otherNFA.getStartState(), visitedStates);
	}
	
	private boolean isEqual(State thisState, State otherState, Set<State> visitedStates) {
		
		if(thisState.getCountTransitions() != otherState.getCountTransitions()) {
			return false;
		}
		
		int i = 0;
		Transition[] t1 = thisState.getSortedTransitions();
		Transition[] t2 = otherState.getSortedTransitions();
		while(i < thisState.getCountTransitions()) {
			if(t1[i].getStart() == t2[i].getStart() && t1[i].getEnd() == t2[i].getEnd()) {
				
				State d1 = t1[i].getDestination();
				State d2 = t2[i].getDestination();

				// Avoid infinite loop
				if(!(visitedStates.contains(d1) && visitedStates.contains(d2))) {
					visitedStates.add(d1);
					visitedStates.add(d2);
					if(!isEqual(d1, d2, visitedStates)) {
						return false;
					}
				}
			}
			i++;
		}
		
		return true;
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
