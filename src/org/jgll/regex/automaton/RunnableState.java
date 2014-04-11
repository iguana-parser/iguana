package org.jgll.regex.automaton;

import org.jgll.regex.matcher.Transitions;
import org.jgll.util.Input;

public class RunnableState {
	
	private Transitions transitions;

	public RunnableState(Transitions transitions) {
		this.transitions = transitions;
	}
	
	public int run(Input input, int index) {
		
		int length = -1;
		
		RunnableState currentState = transitions.move(index);
		
		while (currentState != null) {
			currentState = transitions.move(index);
			length++;
		}
		
		return length;
	}

}
