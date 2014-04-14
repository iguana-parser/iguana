package org.jgll.regex.automaton;

import org.jgll.util.Input;

public interface Action {

	public boolean execute(Input input, int index);
	
}
