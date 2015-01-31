package org.jgll.regex.automaton;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.RegularExpression;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Automaton implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private State startState;
	
	private int[] intervals;
	
	private State[] states;
	
	private boolean deterministic;
	
	private boolean minimized;
	
	private Set<State> finalStates;
	
	private Set<State> rejectStates;
	
	private String name;

	private RunnableAutomaton runnableAutomaton;
	
	public Automaton(State startState) {
		this(startState, "no-name");
	}

	public Automaton(State startState, String name) {

		if(startState == null) {
			throw new IllegalArgumentException("Start symbol cannot be null.");
		}
		
		this.startState = startState;
		this.name = name;
		init(this);
	}

	private void init(Automaton newAutomaton) {
		startState = newAutomaton.getStartState();
		
		deterministic = newAutomaton.deterministic;

		minimized = newAutomaton.minimized;
		
		intervals = AutomatonOperations.getIntervals(this);
		
		AutomatonOperations.setStateIDs(this);
		AutomatonOperations.setTransitionIDs(this);
		
		Set<State> set = AutomatonOperations.getAllStates(this);
		this.states = new State[set.size()];
		
		for(State s : set) {
			states[s.getId()]  = s;
		}
		
		finalStates = AutomatonOperations.getFinalStates(this);
		
		rejectStates = AutomatonOperations.getRejectStates(this);
	}
	
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getFinalStates() {
		return finalStates;
	}
	
	public Set<State> getRejectStates() {
		return rejectStates;
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
	 * Adds the given regular expression to the final states of this
	 * automaton.
	 * 
	 * @param regex the given regular expression
	 */
	public Automaton setRegularExpression(RegularExpression regex) {
		for (State state : finalStates) {
			state.addRegularExpression(regex);
		}
		return this;
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
		
		// Checks whether two NFAs accept the same alphabet (ranges of intervals here) 
		if(!Arrays.equals(intervals, other.intervals)) {
			return false;
		}
		
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
		if(startState.getStateType() == StateType.FINAL) {
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
		
		init(AutomatonOperations.makeDeterministic(this));
		deterministic = true;
		return this;
	}
	public Automaton reverse() {
		init(AutomatonOperations.reverse(this));
		return this;
	}
	
	public Automaton intersection(Automaton a) {
		init(AutomatonOperations.intersection(this, a));
		return this;
	}
	
	public Automaton union(Automaton a) {
		init(AutomatonOperations.union(this, a));
		return this;
	}
	
	public Automaton minimize() {
		
		if(!deterministic) {
			determinize();
		}
		
		if(minimized) {
			return this;
		}
		
		init(AutomatonOperations.minimize(this));
		this.minimized = true;
		return this;
	}
	
	public RunnableAutomaton getRunnableAutomaton() {
		if (runnableAutomaton != null)
			return runnableAutomaton;
		
		determinize();
//		runnableAutomaton = AutomatonOperations.createRunnableAutomaton(this);
		return runnableAutomaton;
	}
	
	public String toJavaCode() {
		return AutomatonOperations.toJavaCode(this);
	}
	
	public Automaton copy() {
		Automaton copy = AutomatonOperations.copy(this);
		copy.deterministic = deterministic;
		copy.minimized = minimized;
		copy.name = name;
		return copy;
	}
	
	public String getName() {
		return name;
	}
	
	public Automaton setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
