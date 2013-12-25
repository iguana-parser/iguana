package org.jgll.regex;

import java.util.BitSet;
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
	
	/**
	 * All characters accepted by this NFA.
	 */
	public BitSet getCharacters() {
		return NFAOperations.getCharacters(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof NFA)) {
			return false;
		}
		
//		NFA other = (NFA) obj;
		
		return super.equals(obj);
	}
	
	public DFA toDFA() {
		throw new UnsupportedOperationException();
	}
	
	public String toJavaCode() {
		return NFAOperations.toJavaCode(this);
	}
	
}
