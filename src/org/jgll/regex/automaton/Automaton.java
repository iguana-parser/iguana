package org.jgll.regex.automaton;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.RegularExpression;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Automaton implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final State startState;
	
	private final State[] states;
	
	private final Set<State> finalStates;
	
	private final boolean minimized;
	
	private final boolean deterministic;
	
	private final CharacterRange[] alphabet;
	
	public Automaton(AutomatonBuilder builder) {
		this.startState = builder.getStartState();
		this.states = builder.getStates();
		this.finalStates = builder.getFinalStates();
		this.minimized = builder.isMinimized();
		this.deterministic = builder.isDeterministic();
		this.alphabet = builder.getAlphabet();
	}
	
	public State getStartState() {
		return startState;
	}
	
	public Set<State> getFinalStates() {
		return finalStates;
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
	
	public CharacterRange[] getAlphabet() {
		return alphabet;
	}
	
	public boolean isDeterministic() {
		return deterministic;
	}
	
	public boolean isMinimized() {
		return minimized;
	}
	
	/**
	 * All characters accepted by this NFA.
	 */
	public BitSet getCharacters() {
		return AutomatonBuilder.getCharacters(this);
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
	
	public Automaton copy() {
		
		final Map<State, State> newStates = new HashMap<>();
		
		final State[] newStartState = new State[1];
		
		AutomatonVisitor.visit(startState, state -> {
			State newState = new State();
			
			newStates.put(state, newState);
			newState.setStateType(state.getStateType());
			
			if(state == startState) {
				newStartState[0] = newState;
			}
		});
		
		AutomatonVisitor.visit(startState, state -> {
				for(Transition transition : state.getTransitions()) {
					State newState = newStates.get(state);
					newState.addTransition(new Transition(transition.getStart(), 
														  transition.getEnd(), 
														  newStates.get(transition.getDestination())));
				}
			});
		
		return builder(newStartState[0]).build();
	}

	
	/**
	 * Determines whether two NFAs are isomorphic. 
	 * The NFAs are first made deterministic before performing the equality check.
	 */
//	@Override
//	public boolean equals(Object obj) {
//		
//		if(obj == this) {
//			return true;
//		}
//		
//		if(!(obj instanceof Automaton)) {
//			return false;
//		}
//		
//		Automaton other = (Automaton) obj;
//		
//		Automaton thisNFA = AutomatonOperations.makeDeterministic(this);
//		Automaton otherNFA = AutomatonOperations.makeDeterministic(other);
//		
//		Set<State> visitedStates = new HashSet<>();
//		
//		return isEqual(thisNFA.getStartState(), otherNFA.getStartState(), visitedStates);
//	}
	
//	private boolean isEqual(State thisState, State otherState, Set<State> visitedStates) {
		
//		if(thisState.getCountTransitions() != otherState.getCountTransitions()) {
//			return false;
//		}
//		
//		int i = 0;
//		Transition[] t1 = thisState.getSortedTransitions();
//		Transition[] t2 = otherState.getSortedTransitions();
//		while(i < thisState.getCountTransitions()) {
//			if(t1[i].getStart() == t2[i].getStart() && t1[i].getEnd() == t2[i].getEnd()) {
//				
//				State d1 = t1[i].getDestination();
//				State d2 = t2[i].getDestination();
//
//				// Avoid infinite loop
//				if(!(visitedStates.contains(d1) && visitedStates.contains(d2))) {
//					visitedStates.add(d1);
//					visitedStates.add(d2);
//					if(!isEqual(d1, d2, visitedStates)) {
//						return false;
//					}
//				}
//			}
//			i++;
//		}
		
//		return true;
//	}
	
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
	
	public AutomatonBuilder builder() {
		return new AutomatonBuilder(this);
	}
	
	public static AutomatonBuilder builder(State startState) {
		return new AutomatonBuilder(startState);
	}
	
	public String toJavaCode() {
		return AutomatonBuilder.toJavaCode(this);
	}	
}
