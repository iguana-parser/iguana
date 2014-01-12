package org.jgll.regex;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Automaton {
	
	private State startState;
	
	private int[] intervals;
	
	private State[] states;
	
	private boolean deterministic;
	
	private boolean minimized;

	public Automaton(State startState) {

		if(startState == null) {
			throw new IllegalArgumentException("Start symbol cannot be null.");
		}
		
		this.startState = startState;
		init();
	}

	private void init() {
		intervals = AutomatonOperations.getIntervals(this);
		
		AutomatonOperations.setStateIDs(this);
		AutomatonOperations.setTransitionIDs(this);
		
		Set<State> set = AutomatonOperations.getAllStates(this);
		this.states = new State[set.size()];
		
		for(State s : set) {
			states[s.getId()]  = s;
		}
	}
	
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getFinalStates() {
		return AutomatonOperations.getFinalStates(this);
	}
	
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
		
		if(!(obj instanceof Automaton)) {
			return false;
		}
		
		Automaton other = (Automaton) obj;
		
		// Checks whether two NFAs accept the same language 
		if(!Arrays.equals(intervals, other.intervals)) {
			return false;
		}
		
		// TODO: Maybe we should change the start symbol of the automaton after it's made 
		// deterministic
		
		Automaton thisNFA = AutomatonOperations.makeDeterministic(this);
		Automaton otherNFA = AutomatonOperations.makeDeterministic(other);
		
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
	
	public boolean isDeterministic() {
		return deterministic;
	}
	
	public boolean isMinimized() {
		return minimized;
	}
	
	/**
	 * Returns true if the language accepted by this automaton is empty. 
	 */
	public boolean isLanguageEmpty() {
		/*
		 * The final sates are calculated from the start state. This means that
		 * all final states returned by calling getFinalStates() are reachable.
		 * The language accepted by this automata is empty, if there are no reachable
		 * final states.
		 */
		
		// Covers the case of the automaton for the empty regular expression
		if(startState.isFinalState()) {
			if(startState.getCountTransitions() == 1 && 
			   startState.getTransitions().iterator().next().isEpsilonTransition()) {
				return true;
			}
		}
		
		return getFinalStates().size() == 0;
	}
	
	public Automaton determinize() {
		
		if(deterministic) {
			return this;
		}
		
		Automaton newAutomaton = AutomatonOperations.makeDeterministic(this);
		startState = newAutomaton.getStartState();
		deterministic = true;
		init();
		return this;
	}
	public Automaton reverse() {
		Automaton reverse = AutomatonOperations.reverse(this);
		startState = reverse.getStartState();
		init();
		return this;
	}
	
	public Automaton intersection(Automaton a) {
		Automaton intersection = AutomatonOperations.intersection(this, a);
		startState = intersection.getStartState();
		init();
		return this;
	}
	
	public Automaton union(Automaton a) {
		Automaton union = AutomatonOperations.union(this, a);
		startState = union.getStartState();
		init();
		return this;
	}
	
	public Automaton minimize() {
		
		if(minimized) {
			return this;
		}
		
		Automaton minimized = AutomatonOperations.minimize(this);
		startState = minimized.getStartState();
		this.minimized = true;
		init();
		return this;
	}
	
	public Matcher getMatcher() {
		if(!deterministic) {
			determinize();
		}
		if(!minimized) {
			minimize();
		}
		return AutomatonOperations.createDFA(this);
	}
	
	public String toJavaCode() {
		return AutomatonOperations.toJavaCode(this);
	}
	
	public Automaton copy() {
		return AutomatonOperations.copy(this);
	}
	
}
