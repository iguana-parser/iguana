package org.jgll.regex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<Transition> transitions;
	
	private Set<State> epsilonClosure;
	
	private Set<StateAction> actions;
	
	/**
	 * The set of regular expressions whose final state is this state.
	 */
	private Set<RegularExpression> regularExpressions;
	
	private boolean finalState;
	
	private int id;
	
	public State() {
		this(false);
	}
	
	public State(boolean finalState) {
		this.transitions = new HashSet<>();
		this.finalState = finalState;
		this.epsilonClosure = new HashSet<>();
		this.actions = new HashSet<>();
		this.regularExpressions = new HashSet<>();
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	/**
	 * Transitions sorted based on their start index.
	 */
	public Transition[] getSortedTransitions() {
		Transition[] array = transitions.toArray(new Transition[] {});
		Arrays.sort(array);
		return array;
	}
	
	/**
	 * Returns all the reachable states by consuming the given input character. 
	 */
	public Set<State> move(int c) {
		Set<State> set = new HashSet<>();
		for(Transition t : transitions) {
			if(t.canMove(c)) {
				set.add(t.getDestination());
			}
		}
		return set;
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
		
		if(transition.isEpsilonTransition()) {
			epsilonClosure.add(transition.getDestination());
		}
	}
	
	public void removeTransition(Transition transition) {
		transitions.remove(transition);
	}
	
	public void removeTransitions(Collection<Transition> c) {
		transitions.removeAll(c);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public Set<StateAction> getActions() {
		return actions;
	}
	
	public State addAction(StateAction action) {
		actions.add(action);
		return this;
	}
	
	public State addActions(Collection<StateAction> actions) {
		this.actions.addAll(actions);
		return this;
	}
	
	public Set<RegularExpression> getRegularExpressions() {
		return regularExpressions;
	}
	
	public State addRegularExpression(RegularExpression regex) {
		regularExpressions.add(regex);
		return this;
	}
	
	public State addRegularExpressions(Collection<RegularExpression> c) {
		regularExpressions.addAll(c);
		return this;
	}
	
	public int getCountTransitions() {
		return transitions.size();
	}
	
	public Set<State> getEpsilonClosure() {
		return epsilonClosure;
	}
	
	@Override
	public String toString() {
		return "State" + id;
	}

}
