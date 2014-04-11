package org.jgll.regex.matcher;

import java.io.Serializable;

import org.jgll.regex.automaton.RunnableState;
import org.jgll.regex.automaton.Transition;

public class ShortIntervalTransitions implements Transitions, Serializable {
	
	private static final long serialVersionUID = 1L;

	private final Transition[] transitionIds;
	
	private final int minimum;
	
	private final int maximum;

	public ShortIntervalTransitions(Transition[] transitions) {
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
	public RunnableState move(int v) {
		if(v < minimum || v > maximum - 1) {
			return null;
		}
		
		Transition transition = transitionIds[v - minimum];
		return null;
	}

}
