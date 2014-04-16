package org.jgll.regex.automaton;

import org.jgll.util.Input;

public class FinalRunnableState extends RunnableState {
	
	public FinalRunnableState(int id, boolean finalState, boolean rejectState) {
		super(id, finalState, rejectState);
	}

	@Override
	public RunnableState move(Input input, int inputIndex) {
		return null;
	}
	
	@Override
	public FinalRunnableState clone() {
		return (FinalRunnableState) super.clone();
	}
	
}
