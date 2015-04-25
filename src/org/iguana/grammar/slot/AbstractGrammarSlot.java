package org.iguana.grammar.slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractGrammarSlot implements GrammarSlot {

	private final int id;
	
	protected final List<Transition> transitions;
	
	public AbstractGrammarSlot(int id) {
		this(id, Collections.emptyList());
	}
	
	public AbstractGrammarSlot(int id, List<Transition> transitions) {
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
