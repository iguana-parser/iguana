package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
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
	public Automaton toAutomaton() {
		return createNFA().addFinalStateActions(actions).addRegularExpression(this);
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);

		Automaton[] nfas = new Automaton[regularExpressions.size()];
		for(int i = 0; i < regularExpressions.size(); i++) {
			nfas[i] = regularExpressions.get(i).toAutomaton();
		}
		
		startState.addTransition(Transition.emptyTransition(nfas[0].getStartState()));
		
		// Middle regular expressions
		int i = 0;
		for(; i < regularExpressions.size() - 1; i++) {
			Automaton nfa = nfas[i];
			Automaton nextNFA = nfas[i+1];
			
			Set<State> finalStates = nfa.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.emptyTransition(nextNFA.getStartState()));
			}
		}
		
		for(State s : nfas[i].getFinalStates()) {
			s.setFinalState(false);
			s.addTransition(Transition.emptyTransition(finalState));
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

	@Override
	public RegularExpression copy() {
		List<RegularExpression> copy = new ArrayList<>();
		for(RegularExpression regex : regularExpressions) {
			copy.add(regex.copy());
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
	
}
