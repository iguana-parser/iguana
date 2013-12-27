package org.jgll.regex;

import org.jgll.util.Input;


public class DFA implements Automaton {
	
	private State startState;

	public DFA(State startState) {
		this.startState = startState;
		AutomatonOperations.setStateIDs(this);
	}

	public int run(Input input, int index) {
		return 0;
	}

	@Override
	public State getStartState() {
		return startState;
	}

	@Override
	public int getCountStates() {
		return AutomatonOperations.getCountStates(this);
	}

}
