package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.CollectionsUtil;

public class Sequence<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> regularExpressions;
	
	public Sequence(List<T> regularExpressions) {
		super(CollectionsUtil.listToString(regularExpressions, " "));
		
		if(regularExpressions.size() == 0) {
			throw new IllegalArgumentException("The number of regular expressions in a sequence should be at least one.");
		}
		this.regularExpressions = regularExpressions;
	}
	
	@SafeVarargs
	public Sequence(T...regularExpressions) {
		this(Arrays.asList(regularExpressions));
	}
	
	public List<T> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(true);

		Automaton[] automatons = new Automaton[regularExpressions.size()];
		for(int i = 0; i < regularExpressions.size(); i++) {
			automatons[i] = regularExpressions.get(i).toAutomaton().copy();
		}
		
		startState.addTransition(Transition.epsilonTransition(automatons[0].getStartState()));
		
		// Middle regular expressions
		int i = 0;
		for(; i < regularExpressions.size() - 1; i++) {
			Automaton nfa = automatons[i];
			Automaton nextNFA = automatons[i+1];
			
			Set<State> finalStates = nfa.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.epsilonTransition(nextNFA.getStartState()));
			}
		}
		
		for(State s : automatons[i].getFinalStates()) {
			s.setFinalState(false);
			s.addTransition(Transition.epsilonTransition(finalState));
		}
				
		return new Automaton(startState);
	}
	
	@Override
	public boolean isNullable() {
		for(RegularExpression regex : regularExpressions) {
			if(!regex.isNullable()) {
				return false;
			}
		}
		return true;
	}
	
	public int size() {
		return regularExpressions.size();
	}
	
	public T get(int index) {
		return regularExpressions.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Sequence<T> copy() {
		List<T> copy = new ArrayList<>();
		for(T regex : regularExpressions) {
			copy.add((T) regex.copy());
		}
		return new Sequence<>(copy);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof Sequence)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Sequence<T> other = (Sequence<T>) obj;
		
		return regularExpressions.equals(other.regularExpressions);
	}
	
	@Override
	public int hashCode() {
		return regularExpressions.hashCode();
	}

	@Override
	public Iterator<T> iterator() {
		return regularExpressions.iterator();
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		for(T t : regularExpressions) {
			firstSet.addAll(t.getFirstSet());
			if(!t.isNullable()) {
				break;
			}
		}
		return firstSet;
	}
	
}
