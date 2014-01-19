package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
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
	public Automaton toAutomaton() {
		return createNFA().addFinalStateActions(actions).addRegularExpression(this);
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		for(RegularExpression regexp : regularExpressions) {
			Automaton nfa = regexp.toAutomaton();
			startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
			
			Set<State> finalStates = nfa.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.emptyTransition(finalState));				
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
	
}
