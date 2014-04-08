package org.jgll.regex.automaton;

import org.jgll.util.Input;

public class FalseTransitionAction implements TransitionAction {

	@Override
	public boolean execute(Input input, int index) {
		return false;
	}

}
