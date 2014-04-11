package org.jgll.regex.matcher;

import org.jgll.regex.automaton.RunnableState;

public interface Transitions {
	
	public RunnableState move(int v);
}
