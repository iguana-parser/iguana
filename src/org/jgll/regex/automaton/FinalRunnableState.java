package org.jgll.regex.automaton;

import org.jgll.util.Input;

public class FinalRunnableState extends RunnableState {
	
	public FinalRunnableState(boolean finalState) {
		super(finalState);
	}

	@Override
	public RunnableState move(Input input, int inputIndex) {
		return null;
	}
	
}
