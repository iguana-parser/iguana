package org.jgll.regex.automaton;

import org.jgll.util.Input;


public class RejectState extends RunnableState {

	private static RejectState instance;
	
	public static RejectState getInstance() {
		if (instance == null) {
			instance = new RejectState();
		}
		return instance;
	}
	
	private RejectState() {
		super(-1, StateType.REJECT);
	}
	
	@Override
	public RunnableState move(Input input, int inputIndex) {
		return null;
	}

}
