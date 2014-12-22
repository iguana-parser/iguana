package org.jgll.regex.automaton;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jgll.util.Input;

public class LargeIntervalTransitions implements Transitions, Serializable {

	private static final long serialVersionUID = 1L;

	private final TreeMap<Integer, Transition> transitionsMap;
	
	private final TreeMap<Integer, RunnableState> states;

	public LargeIntervalTransitions(Transition[] transitions, Map<Transition, RunnableState> map) {
		transitionsMap = new TreeMap<>();
		states = new TreeMap<>();
		
		for (Transition t : transitions) {
			transitionsMap.put(t.getStart(), t);
			states.put(t.getStart(), map.get(t));
			if(!transitionsMap.containsKey(t.getEnd() + 1)) {
				transitionsMap.put(t.getEnd() + 1, null);
				states.put(t.getEnd() + 1, null);
			}
		}
	}
	
	@Override
	public RunnableState move(Input input, int inputIndex) {
		
		int v = input.charAt(inputIndex);
		
		Entry<Integer, Transition> e = transitionsMap.floorEntry(v);
		
		Transition transition = e == null ? null : e.getValue();
		
		if (transition == null) return null;
		
		if (transition.executeActions(input, inputIndex + 1)) {
			return RejectState.getInstance();
		}
		
		return states.floorEntry(v).getValue();
	}
}
