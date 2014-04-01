package org.jgll.regex.automaton;

import java.io.Serializable;

public interface StateAction extends Serializable {
	
	public void execute(int length, int state);

}
