package org.jgll.regex.automaton;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState {
	
	private Transitions transitions;

	public RunnableState(Transitions transitions) {
		this.transitions = transitions;
	}
	
	public RunnableState run(Input input, int index) {
		Transition transition = transitions.getTransition(index);
		return null;
	}

}
