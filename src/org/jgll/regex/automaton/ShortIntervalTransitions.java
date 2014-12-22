package org.jgll.regex.automaton;

import java.io.Serializable;
import java.util.Map;

import org.jgll.util.Input;

public class ShortIntervalTransitions implements Transitions, Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * transitionsMap[c] holds the corresponding transition for the input value c. 
	 */
	private final Transition[] transitionsMap;
	
	/**
	 * states[c] holds the next state for the input value c.
	 */
	private final RunnableState[] states;
	
	private final int minimum;
	
	private final int maximum;

	public ShortIntervalTransitions(Transition[] transitions, Map<Transition, RunnableState> map) {
		minimum = transitions[0].getStart();
		maximum = transitions[transitions.length - 1].getEnd();
		
		transitionsMap = new Transition[maximum - minimum + 1];
		states = new RunnableState[maximum - minimum + 1];
		
		for (Transition t : transitions) {
			for(int i = t.getStart(); i <= t.getEnd(); i++) {
				transitionsMap[i - minimum] = t;
				states[i - minimum] = map.get(t);
			}
		}
 	}
	
	@Override
	public RunnableState move(Input input, int inputIndex) {
		int v = input.charAt(inputIndex);
		if(v < minimum || v > maximum) {
			return null;
		}
		
		Transition transition = transitionsMap[v - minimum];
		
		if(transition == null) return null;
		
		if (transition.executeActions(input, inputIndex + 1)) {
			return RejectState.getInstance();
		}
		
		return states[v - minimum];
	}

}
