package org.jgll.regex.automaton;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;

public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<Transition> transitions;
	
	/**
	 * Epsilon closure set for this set, i.e., the set of states reachable 
	 * from epsilon transitions from this state.
	 */
	private Set<State> epsilonClosure;
	
	/**
	 * A map from the start interval of each transition to the transition.
	 * This map is used for fast lookup of transitions.
	 */
	private Map<Integer, Transition> transitionsMap;
	
	/**
	 * The set of regular expressions whose final state is this state.
	 */
	private Set<RegularExpression> regularExpressions;
	
	private boolean finalState;
	
	private boolean rejectState;
	
	private int id;
	
	private Set<Action> actions;
	
	public State() {
		this(false, new HashSet<Action>());
	}
	
	public State(boolean finalState) {
		this(finalState, new HashSet<Action>());
	}
	
	public State(Set<Action> actions) {
		this(false, actions);
	}
	
	public State(boolean finalState, Set<Action> actions) {
		this.transitions = new HashSet<>();
		this.transitionsMap = new HashMap<>();
		this.finalState = finalState;
		this.epsilonClosure = new HashSet<>();
		this.regularExpressions = new HashSet<>();
		this.actions = actions;
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
	
	public boolean isRejectState() {
		return rejectState;
	}
	
	public void setRejectState(boolean rejectState) {
		this.rejectState = rejectState;
	}
	
	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}
	
	public void addTransition(Transition transition) {
		transitionsMap.put(transition.getStart(), transition);
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
	
	public Transition getTransition(int start) {
		return transitionsMap.get(start);
	}
	
	public boolean hasTransition(int start) {
		return transitionsMap.containsKey(start);
	}
	
	@Override
	public String toString() {
		return "State" + id + " " + (actions.isEmpty() ? "" : actions);
	}
	
	public void addAction(Action action) {
		if (action != null) {
			actions.add(action);
		}
	}
	
	public void addActions(Set<Action> actions) {
		this.actions.addAll(actions);
	}
	
	public boolean executeActions(Input input, int index) {
		
		for (Action action : actions) {
			if(action.execute(input, index)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Set<Action> getActions() {
		return actions;
	}
}
