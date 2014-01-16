package org.jgll.regex;

import java.io.Serializable;
import java.util.List;

import org.jgll.util.Input;

public abstract class AbstractMatcher implements Matcher, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final int[][] transitionTable;
	
	private final boolean[] endStates;
	
	private final List<MatchAction>[] matchActions;

	private final int startStateId;
	
	protected final int[] intervals;
	
	private int id;
	
	private int mode = LONGEST_MATCH;

	public AbstractMatcher(int[][] transitionTable, 
						   boolean[] endStates, 
						   int startStateId, 
						   int[] intervals,
						   List<MatchAction>[] matchActions) {
		
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.startStateId = startStateId;
		this.intervals = intervals;
		this.matchActions = matchActions;
	}

	@Override
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}
	
	@Override
	public boolean match(Input input, int start, int end) {
		return match(input, 0) == end - start;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public int shortestMatch(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		// If the start state is an accepting state, we can always match a string with length 0.
		if(endStates[stateId]) {
			return 0;
		}
		
		for(int i = inputIndex; i < input.length(); i++) {
			int transitionId = getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				return length;
			}
		}
		
		return -1;
	}

	@Override
	public int match(Input input, int inputIndex) {
		if(mode == LONGEST_MATCH) {
			return longestMatch(input, inputIndex);
		} else {
			return shortestMatch(input, inputIndex);
		}
	}

	public int longestMatch(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		int previousId = stateId;
		
		int maximumMatched = -1;
		
		// If the start state is an accepting state, we can always match a string with length 0.
		if(endStates[stateId]) {
			maximumMatched = 0;
		}
		
		for(int i = inputIndex; i < input.length(); i++) {
			int transitionId = getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				previousId = stateId;
				break;
			}
			
			previousId = stateId;
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
			}
		}
		
		// Match found
		if(maximumMatched >= 0) {
			for(MatchAction action : matchActions[previousId]) {
				action.execute(maximumMatched, previousId);
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
			int transitionId = getTransitionId(input.charAt(i));
			
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
	
	protected abstract int getTransitionId(int c);

}
