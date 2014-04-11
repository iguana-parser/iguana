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

public class RegexAlt<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> regularExpressions;
	
	public RegexAlt(List<T> regularExpressions) {
		super("(" + CollectionsUtil.listToString(regularExpressions, " | ") + ")");
		
		if(regularExpressions == null) {
			throw new IllegalArgumentException("The list of regular expressions cannot be null.");
		}
		
		if(regularExpressions.size() == 0) {
			throw new IllegalArgumentException("The list of regular expressions cannot be empty.");
		}
		
		this.regularExpressions = regularExpressions;
	}
	
	@SafeVarargs
	public RegexAlt(T...regularExpressions) {
		this(Arrays.asList(regularExpressions));
	}
	
	public List<T> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(true);
		
		for(RegularExpression regexp : regularExpressions) {
			Automaton automaton = regexp.toAutomaton().copy();
			startState.addTransition(Transition.epsilonTransition(automaton.getStartState()));
			
			Set<State> finalStates = automaton.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.epsilonTransition(finalState));				
			}
		}
		
		return new Automaton(startState);
	}

	@Override
	public boolean isNullable() {
		for(RegularExpression regex : regularExpressions) {
			if(regex.isNullable()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RegexAlt<T> copy() {
		List<T> copy = new ArrayList<>();
		for(RegularExpression regex : regularExpressions) {
			copy.add((T) regex.copy());
		}
		return new RegexAlt<>(copy);
	}

	@Override
	public Iterator<T> iterator() {
		return regularExpressions.iterator();
	}
	
	public int size() {
		return regularExpressions.size();
	}
	
	public T get(int index) {
		return regularExpressions.get(index);
	}
	
	@Override
	public boolean equals(Object obj) {
	
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof RegexAlt)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		RegexAlt other = (RegexAlt) obj;
		
		return other.regularExpressions.equals(regularExpressions);
	}
	
	@Override
	public int hashCode() {
		return regularExpressions.hashCode();
	}

	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		for(T t : regularExpressions) {
			firstSet.addAll(t.getFirstSet());
		}
		return firstSet;
	}
	
}
