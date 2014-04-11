package org.jgll.regex.automaton;

import org.jgll.util.Input;

public class FinalRunnableState extends RunnableState {
	
	public FinalRunnableState(boolean finalState, boolean rejectState) {
		super(finalState, rejectState);
	}

	@Override
	public RunnableState move(Input input, int inputIndex) {
		return null;
	}
	
}
