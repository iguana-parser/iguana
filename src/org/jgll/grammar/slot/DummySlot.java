package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.util.Input;


public class DummySlot implements GrammarSlot {

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return "new DummySlot()";
	}

	@Override
	public void reset(Input input) {
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}
	
	@Override
	public int getId() {
		return 0;
	}

}
