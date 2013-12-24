package org.jgll.regex;

import java.util.HashSet;
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
}
