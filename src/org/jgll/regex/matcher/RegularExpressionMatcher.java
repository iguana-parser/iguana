package org.jgll.regex.matcher;

import java.io.Serializable;

import org.jgll.util.Input;

public class RegularExpressionMatcher implements Matcher, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final int[][] transitionTable;
	
	protected final boolean[] endStates;
	
	protected final boolean[] rejectStates;
	
	protected final int startStateId;
	
	protected int id;
	
	protected int mode = LONGEST_MATCH;

	private Transitions transitions;

	public RegularExpressionMatcher(int[][] transitionTable, 
						   boolean[] endStates, 
						   boolean[] rejectStates,
						   int startStateId,
						   Transitions transitions) {
		
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.rejectStates = rejectStates;
		this.startStateId = startStateId;
		this.transitions = transitions;
	}

	@Override
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}
	
	@Override
	public boolean match(Input input, int start, int end) {
		return match(input, start) == end - start;
	}
	
	@Override
	public int match(Input input, int inputIndex) {
		
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		// If the start state is an accepting state, we can always match a string with length 0.
		if(endStates[stateId] && !rejectStates[stateId]) {
			maximumMatched = 0;

			// Handling epsilon matching the empty input
			if(input.isEmpty()) {
				return maximumMatched;
			}
			
			// If the input index is EOF and the current state is an end state.
			if(input.charAt(inputIndex) == 0) {
				return 0;
			}
		}		
		
		// Handling the EOF character
		if(input.charAt(inputIndex) == 0) {
			int transitionId = transitions.getTransitionId(0);

			if(transitionId == -1) {
				return -1;
			}

			stateId = transitionTable[stateId][transitionId];
			
			if(stateId == -1) {
				return -1;
			}
			
			if(endStates[stateId] && !rejectStates[stateId]) {
				return 0;
			}
			
			return -1;
		}
		
		for(int i = inputIndex; i < input.length(); i++) {
			int transitionId = transitions.getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
				if(mode == SHORTEST_MATCH) {
					break;
				}
			}
			
			// If ended up in a reject state, clean up the latest final state
			if(rejectStates[stateId]) {
				maximumMatched = -1;
			}
			
		}
		
		return maximumMatched;
	}
	
	@Override
	public int matchBackwards(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		if(input.isEmpty() && endStates[stateId]) {
			return 0;
		}
		
		for(int i = inputIndex; i >= 0; i--) {
			int transitionId = transitions.getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
			}
		}
		
		return maximumMatched;
	}
	
	@Override
	public Matcher setMode(int mode) {
		this.mode = mode; 
		return this;
	}
	
	@Override
	public Matcher copy() {
		return new RegularExpressionMatcher(transitionTable, endStates, rejectStates, startStateId, transitions);
	}
	
}
