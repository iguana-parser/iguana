package org.jgll.regex.matcher;

import org.jgll.regex.automaton.RunnableState;
import org.jgll.util.Input;

public interface Transitions {
	
	public RunnableState move(Input input, int inputIndex);
}
