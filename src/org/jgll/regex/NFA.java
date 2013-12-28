package org.jgll.regex;

import java.util.BitSet;


public class NFA implements Automaton {
	
	private State startState;
	private State endState;

	public NFA(State startState, State endState) {
		this.startState = startState;
		this.endState = endState;
		AutomatonOperations.setStateIDs(this);
	}
	
	@Override
	public State getStartState() {
		return startState;
	}
	
	public State getEndState() {
		return endState;
	}
	
	@Override
	public int getCountStates() {
		return AutomatonOperations.getCountStates(this);
	}
	
	/**
	 * All characters accepted by this NFA.
	 */
	public BitSet getCharacters() {
		return AutomatonOperations.getCharacters(this);
	}
	
	public Integer[] getIntervals() {
		return AutomatonOperations.getIntervalPoints(this);
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
		return AutomatonOperations.convertNFAtoDFA(this);
	}
	
	public String toJavaCode() {
		return AutomatonOperations.toJavaCode(this);
	}
	
}
