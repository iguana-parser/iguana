package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbstractGrammarSlot implements GrammarSlot {

	private final int id;
	
	protected final List<Transition> transitions;

	public AbstractGrammarSlot(int id) {
		this(id, new HashSet<>());
	}
	
	public AbstractGrammarSlot(int id, Set<Transition> transitions) {
		this.id = id;
		this.transitions = new ArrayList<>(transitions);
	}

	@Override
	public boolean addTransition(Transition transition) {
		return transitions.add(transition);
	}

	@Override
	public Iterable<Transition> getTransitions() {
		return transitions;
	}

	@Override
	public int getId() {
		return id;
	}

}
