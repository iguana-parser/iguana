package org.jgll.regex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class NFA {
	
	private State startState;
	private State endState;

	public NFA(State startState, State endState) {
		this.startState = startState;
		this.endState = endState;
	}
	
	public State getStartState() {
		return startState;
	}
	
	public State getEndState() {
		return endState;
	}
	
	public int getCountStates() {
		Set<State> visitedStates = new HashSet<>();
		Stack<State> stack = new Stack<>();
		
		stack.push(startState);
		visitedStates.add(startState);
		
		int count = 0;
		
		while(!stack.isEmpty()) {
			State state = stack.pop();
			count++;
			
			for(Transition transition : state.getTransitions()) {
				State destination = transition.getDestination();
				if(!visitedStates.contains(destination)) {
					visitedStates.add(destination);
					stack.push(destination);
				}
			}
		}
		
		return count;
	}
	
	public DFA toDFA() {
		throw new UnsupportedOperationException();
	}
	
	public String toJavaCode() {
		StringBuilder sb = new StringBuilder();
		Map<State, Integer> visitedStates = new HashMap<>();
		visitedStates.put(startState, 1);
		toJavaCode(startState, sb, visitedStates);
		return sb.toString();
	}
	
	private void toJavaCode(State state, StringBuilder sb, Map<State, Integer> visitedStates) {
		sb.append("State state" + visitedStates.get(state) + " = new State();\n");
		for(Transition transition : state.getTransitions()) {
			State destination = transition.getDestination();
			
			if(!visitedStates.keySet().contains(destination)) {
				visitedStates.put(destination, visitedStates.size() + 1);
				toJavaCode(destination, sb, visitedStates);
			}
			
			sb.append("state" + visitedStates.get(state) + ".addTransition(new Transition(" + transition.getStart() + 
					                                               ", " + transition.getEnd() + ", state" + visitedStates.get(destination) + ");\n");
		}
	}
	
}
