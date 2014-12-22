package org.jgll.regex.automaton;

import org.jgll.util.Input;

public interface Transitions {
	
	public RunnableState move(Input input, int inputIndex);
}
