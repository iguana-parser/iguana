package org.jgll.grammar.slot;

import java.util.HashSet;
import java.util.Set;


public abstract class AbstractGrammarSlot implements GrammarSlot {

	protected final Set<Transition> transitions;

	public AbstractGrammarSlot() {
		this.transitions = new HashSet<>();
	}
	
	public AbstractGrammarSlot(Set<Transition> transitions) {
		this.transitions = new HashSet<>(transitions);
	}

	@Override
	public boolean addTransition(Transition transition) {
		return transitions.add(transition);
	}

	@Override
	public Set<Transition> getTransitions() {
		return transitions;
	}

}
