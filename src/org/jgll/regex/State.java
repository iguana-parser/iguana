package org.jgll.regex;

import java.util.HashSet;
import java.util.Set;

public class State {
	
	private Set<Transition> transitions;
	
	public State() {
		transitions = new HashSet<>();
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}

}
