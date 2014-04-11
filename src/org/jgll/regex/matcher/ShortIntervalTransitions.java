package org.jgll.regex.matcher;

import java.io.Serializable;
import java.util.Map;

import org.jgll.regex.automaton.RunnableState;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.Input;

public class ShortIntervalTransitions implements Transitions, Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Transition[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;

	private final Map<State, RunnableState> map;

	public ShortIntervalTransitions(Transition[] transitions, Map<State, RunnableState> map) {
		this.map = map;
		minimum = transitions[0].getStart();
		maximum = transitions[transitions.length - 1].getEnd();
		
		transitionIds = new Transition[maximum - minimum + 1];
		int k = 0;
		for (Transition t : transitions) {
			for(int i = t.getStart(); i < t.getEnd(); i++) {
				transitionIds[k++] = t;
			}
		}
 	}
	
	@Override
	public RunnableState move(Input input, int inputIndex) {
		int v = input.charAt(inputIndex);
		if(v < minimum || v > maximum - 1) {
			return null;
		}
		
		Transition transition = transitionIds[v - minimum];
		
		if (transition.executeActions(input, inputIndex)) {
			return null;
		}
		
		return map.get(transition.getDestination());
	}

}
