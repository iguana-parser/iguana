package org.jgll.regex.automaton;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.Input;

public class FalseTransitionAction implements Action {

	@Override
	public boolean execute(Input input, int index) {
		return false;
	}

	@Override
	public Set<Condition> getConditions() {
		return Collections.emptySet();
	}

}
