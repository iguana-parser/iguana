package org.jgll.regex.automaton;

import java.io.Serializable;

import org.jgll.util.Input;

public interface RunnableAutomaton extends Serializable {
	
	public boolean match(Input input);
	
	public boolean match(Input input, int start, int end);
	
	public int match(Input input, int inputIndex);
	
	public int matchBackwards(Input input, int inputIndex);
	
}
